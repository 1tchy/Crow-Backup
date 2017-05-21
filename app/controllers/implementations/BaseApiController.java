package controllers.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import controllers.actions.ExplicitAction;
import controllers.actions.WithExplicitAction;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import play.db.jpa.Transactional;
import play.inject.Injector;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class BaseApiController extends Controller {

    private final Map<String, ApiCall> serverMethods = new HashMap<>();

    @Inject
    public BaseApiController(Injector injector) {
        //Für alle Implementationen der Server Interfaces...
        Set<Class<?>> interfaces = new Reflections("models.interfaces", new SubTypesScanner(false)).getSubTypesOf(Object.class);
        for (Class<?> anInterface : interfaces) {
            Object implementation = injector.instanceOf(anInterface);
            //...bzw dessen Methoden...
            for (Method method : implementation.getClass().getDeclaredMethods()) {
                //...eine Funktion vorbereiten, mit welcher diese aufgerufen werden kann.
                Function<Object[], CompletionStage<Result>> function = (Object[] parameters) -> {
                    try {
                        CompletionStage<?> result = (CompletionStage<?>) method.invoke(implementation, parameters);
                        return result.thenApplyAsync(resultJsonObject -> resultJsonObject == null ? noContent() : ok(Json.toJson(resultJsonObject)));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return CompletableFuture.completedFuture(badRequest("Fehler QJRgagH7XSUwuAPn"));
                    }
                };

                Function<Object[], CompletionStage<Result>> securedFunction = wrapWithSecurity(function, method, injector);
                ApiCall apiCall = new ApiCall(anInterface.getSimpleName(), method.getName(), securedFunction, method.getParameterTypes());
                serverMethods.put(apiCall.getCallName(), apiCall);
            }
        }
    }

    private static Function<Object[], CompletionStage<Result>> wrapWithSecurity(Function<Object[], CompletionStage<Result>> function, Method method, Injector injector) {
        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            WithExplicitAction withExplicitAction = methodAnnotation.annotationType().getDeclaredAnnotation(WithExplicitAction.class);
            if (withExplicitAction != null) {
                ExplicitAction explicitAction = injector.instanceOf(withExplicitAction.value());
                return (Object[] obj) -> explicitAction.call(ctx(), ctx -> {
                    Http.Context.current.set(ctx);
                    return function.apply(obj);
                });
            }
        }
        //else (Methode nicht annotiert)
        return function;
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> apiCall(String iface, String methodName) {
        JsonNode json = request().body().asJson();
        String callName = ApiCall.getCallName(iface, methodName);
        ApiCall apiCall = serverMethods.get(callName);
        if (apiCall != null) {
            return apiCall.call(json);
        } else {
            return CompletableFuture.completedFuture(notFound(methodName));
        }
    }

    private static class ApiCall {

        private final String iface;
        private final String method;
        private final Function<Object[], CompletionStage<Result>> callable;
        private final Class<?>[] types;

        private ApiCall(String iface, String method, Function<Object[], CompletionStage<Result>> callable, Class<?>[] types) {
            this.iface = iface;
            this.method = method;
            this.callable = callable;
            this.types = types;
        }

        public String getCallName() {
            return getCallName(iface, method);
        }

        @NotNull
        private static String getCallName(String iface, String method) {
            return iface + "/" + method;
        }

        public CompletionStage<Result> call(JsonNode json) {
            if (!json.isArray()) {
                return CompletableFuture.completedFuture(badRequest(json + " ist ungültig"));
            }
            try {
                return callable.apply(mapJsonListToObjects((ArrayNode) json, types));
            } catch (JsonProcessingException e) {
                return CompletableFuture.completedFuture(badRequest(json + " ist ungültig für " + getCallName()));
            }
        }

        private Object[] mapJsonListToObjects(ArrayNode nodes, Class<?>[] types) throws JsonProcessingException {
            Object[] ret = new Object[types.length];
            ObjectMapper mapper = Json.mapper();
            for (int i = 0; i < types.length; i++) {
                ret[i] = mapper.treeToValue(nodes.get(i), types[i]);
            }
            return ret;
        }

    }

}

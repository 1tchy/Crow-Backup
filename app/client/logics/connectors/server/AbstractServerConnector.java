package client.logics.connectors.server;

import client.logics.connectors.server.implementations.TestServerConnector;
import controllers.routes;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.api.mvc.Call;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationLoader;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Http;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class AbstractServerConnector {

    private final WSClient ws;
    private final ServerAuthentication serverAuthentication;

    public AbstractServerConnector(WSClient ws, ServerAuthentication serverAuthentication) {
        this.ws = ws;
        this.serverAuthentication = serverAuthentication;
    }

    /**
     * TODO: nur als Beispiel, später wieder entfernen
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Application app = new GuiceApplicationLoader().builder(ApplicationLoader.Context.create(Environment.simple())).build();
        Injector injector = app.injector();
        System.out.println(injector.instanceOf(TestServerConnector.class).helloWorld("Erde").toCompletableFuture().get());
        app.getWrappedApplication().stop();
    }

    protected <R> CompletionStage<R> apiCall(Class<R> returnType, Object... param) {
        Method method = getCallingMethod();
        Call call = routes.BaseApiController.apiCall(method.getDeclaringClass().getSimpleName(), method.getName());
        String url = call.absoluteURL(false, "localhost:9000");
        WSRequest request = ws.url(url);
        request.setContentType("application/json");
        serverAuthentication.getAuthenticationToken().ifPresent(request::setAuth);
        request.setBody(Json.toJson(param));
        request.setMethod(call.method());
        return request.execute().thenApply(
                wsResponse -> wsResponse.getStatus() == Http.Status.NO_CONTENT ? null : wsResponse.asJson()
        ).thenApply(
                returnJson -> returnJson == null ? null : Json.fromJson(returnJson, returnType)
        );
    }

    protected Method getCallingMethod() {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        try {
            Class<?> klasse = Class.forName(stackTrace.getClassName());
            for (Method method : klasse.getDeclaredMethods()) {
                if (method.getName().equals(stackTrace.getMethodName())) {
                    return method;
                }
            }
            throw new RuntimeException("Methode '" + stackTrace.getMethodName() + "' nicht gefunden in " + klasse);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Klasse nicht gefunden für " + stackTrace, e);
        }
    }

    protected void doLogout() {
        serverAuthentication.logout();
    }

    protected void doLogin(String token) {
        serverAuthentication.setAuthenticationToken(token);
    }
}

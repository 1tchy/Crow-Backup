package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import controllers.actions.AuthenticatedRequest;
import controllers.actions.ExplicitAction;
import controllers.actions.WithExplicitAction;
import controllers.actions.WithUser;
import models.ServerInterface;
import models.user.User;
import org.jetbrains.annotations.NotNull;
import play.db.jpa.Transactional;
import play.inject.Injector;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.LoginTokenService;
import services.PasswordService;
import services.PersistenceService;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class ApiController extends Controller implements ServerInterface {

    private final PersistenceService persistenceService;
    private final PasswordService passwordService;
    private final LoginTokenService loginTokenService;
    private final Injector injector;

    @Inject
    public ApiController(PersistenceService persistenceService, PasswordService passwordService, LoginTokenService loginTokenService, Injector injector) {
        this.persistenceService = persistenceService;
        this.passwordService = passwordService;
        this.loginTokenService = loginTokenService;
        this.injector = injector;
    }

    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> apiCall(String methodName) {
        try {
            JsonNode json = request().body().asJson();
            if (!json.isArray()) {
                return CompletableFuture.completedFuture(badRequest(json + " ist ungültig"));
            }
            final Method method = getMethod(methodName);
            Object[] parameters;
            try {
                parameters = mapJsonListToObjects((ArrayNode) json, method.getParameterTypes());
            } catch (JsonProcessingException e) {
                return CompletableFuture.completedFuture(badRequest(json + " ist ungültig für " + methodName));
            }
            return callWithOrWithoutExplicitAnnotation(method, parameters);
        } catch (NoSuchMethodException e) {
            return CompletableFuture.completedFuture(notFound(methodName));
        }
    }

    private CompletionStage<Result> callWithOrWithoutExplicitAnnotation(Method method, Object[] parameters) {
        Supplier<CompletionStage<Result>> call = () -> {
            try {
                return mapResult((CompletionStage<?>) method.invoke(this, parameters));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return CompletableFuture.completedFuture(badRequest("Fehler QJRgagH7XSUwuAPn"));
            }
        };

        for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
            WithExplicitAction withExplicitAction = methodAnnotation.annotationType().getDeclaredAnnotation(WithExplicitAction.class);
            if (withExplicitAction != null) {
                ExplicitAction explicitAction = injector.instanceOf(withExplicitAction.value());
                return explicitAction.call(ctx(), ctx -> {
                    Http.Context.current.set(ctx);
                    return call.get();
                });
            }
        }
        //else (Methode nicht annotiert)
        return call.get();
    }

    private CompletionStage<Result> mapResult(CompletionStage<?> promise) {
        return promise.thenApplyAsync(resultJsonObject -> resultJsonObject == null ? noContent() : ok(Json.toJson(resultJsonObject)));
    }

    private Object[] mapJsonListToObjects(ArrayNode nodes, Class<?>[] types) throws JsonProcessingException {
        Object[] ret = new Object[types.length];
        ObjectMapper mapper = Json.mapper();
        for (int i = 0; i < types.length; i++) {
            ret[i] = mapper.treeToValue(nodes.get(i), types[i]);
        }
        return ret;
    }

    private Method getMethod(String methodName) throws NoSuchMethodException {
        for (Method method : getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException();
    }

    @Override
    public CompletionStage<String> helloWorld(String name) {
        return CompletableFuture.completedFuture(name == null ? null : "Hallo " + name);
    }

    @Override
    public CompletionStage<User> createUser(String mail, char[] password) {
        return persistenceService.asyncWithTransaction(false, () -> {
            User user = new User();
            user.setMail(mail);
            user.setPasswordHash(passwordService.createHash(password));
            persistenceService.persist(user);
            return toUserForClient(user);
        });
    }

    @NotNull
    private User toUserForClient(User user) {
        persistenceService.detach(user);
        user.setPasswordHash(null);
        return user;
    }

    @Override
    public CompletionStage<String> login(String mail, char[] password) {
        return persistenceService.asyncWithTransaction(true, () -> {
            Optional<User> user = persistenceService.readOne(User.class, "mail", mail);
            if (user.isPresent() && passwordService.isPasswordCorrect(user.get().getPasswordHash(), password)) {
                return loginTokenService.create(user.get());
            } else {
                return null;
            }
        });
    }

    @Override
    public CompletionStage<Void> logout() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @WithUser
    public CompletionStage<User> getCurrentUser() {
        AuthenticatedRequest request = (AuthenticatedRequest) request();
        return CompletableFuture.completedFuture(request.getAuthenticatedUser());
    }

    @Override
    @WithUser
    public CompletionStage<Void> changeUserPassword(char[] newPassword) {
        AuthenticatedRequest request = (AuthenticatedRequest) request();
        return persistenceService.asyncWithTransaction(false, () -> {
            User dbUser = persistenceService.readUnique(User.class, request.getAuthenticatedUser().getId());
            dbUser.setPasswordHash(passwordService.createHash(newPassword));
            persistenceService.persist(dbUser);
            return null;
        });
    }

}

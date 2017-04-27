package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.ServerInterface;
import models.user.User;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.PasswordService;
import services.PersistenceService;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ApiController extends Controller implements ServerInterface {

    private final PersistenceService persistenceService;
    private final PasswordService passwordService;

    @Inject
    public ApiController(PersistenceService persistenceService, PasswordService passwordService) {
        this.persistenceService = persistenceService;
        this.passwordService = passwordService;
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
            try {
                @SuppressWarnings("unchecked") CompletionStage<?> result = (CompletionStage<?>) method.invoke(this, parameters);
                return result.thenApplyAsync(resultJsonObject -> resultJsonObject == null ? noContent() : ok(Json.toJson(resultJsonObject)));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return CompletableFuture.completedFuture(badRequest("Fehler QJRgagH7XSUwuAPn"));
            }
        } catch (NoSuchMethodException e) {
            return CompletableFuture.completedFuture(notFound(methodName));
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
    public CompletionStage<User> login(String mail, char[] password) {
        return persistenceService.asyncWithTransaction(true, () -> {
            Optional<User> user = persistenceService.readOne(User.class, "mail", mail);
            if (user.isPresent() && passwordService.isPasswordCorrect(user.get().getPasswordHash(), password)) {
                return toUserForClient(user.get()); //todo: login merken
            } else {
                return null;
            }
        });
    }

    @Override
    public CompletionStage<Void> logout(User user) {
        throw new NotImplementedException("logout " + user);
    }

    @Override
    public CompletionStage<User> getCurrentUser() {
        throw new NotImplementedException("getCurrentUser");
    }

    @Override
    public CompletionStage<Void> changeUserPassword(User user, char[] newPassword) {
        return persistenceService.asyncWithTransaction(false, () -> {
            User dbUser = persistenceService.readUnique(User.class, user.getId());
            dbUser.setPasswordHash(passwordService.createHash(newPassword));
            persistenceService.persist(dbUser);
            return null;
        });
    }

}

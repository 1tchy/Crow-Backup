package client.logic.connectors.server;

import models.ServerInterface;
import models.user.User;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.api.mvc.Call;
import play.inject.Injector;
import play.inject.guice.GuiceApplicationLoader;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;


public class ServerConnector implements ServerInterface {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Application app = new GuiceApplicationLoader().builder(ApplicationLoader.Context.create(Environment.simple())).build();
        Injector injector = app.injector();
        System.out.println(injector.instanceOf(ServerConnector.class).helloWorld("Erde").toCompletableFuture().get());
        app.getWrappedApplication().stop();
    }

    @Inject
    private WSClient ws;

    @Override
    public CompletionStage<String> helloWorld(String name) {
        return apiCall(String.class, name);
    }

    @Override
    public CompletionStage<User> createUser(String mail, char[] password) {
        return apiCall(User.class, mail, password);
    }

    @Override
    public CompletionStage<User> login(String mail, char[] password) {
        return apiCall(User.class, mail, password);
    }

    @Override
    public CompletionStage<Void> logout(User user) {
        return apiCall(Void.class, user);
    }

    @Override
    public CompletionStage<User> getCurrentUser() {
        return apiCall(User.class);
    }

    @Override
    public CompletionStage<Void> changeUserPassword(User user, char[] newPassword) {
        return apiCall(Void.class, user, newPassword);
    }

    private <R> CompletionStage<R> apiCall(Class<R> returnType, Object... param) {
        String methodName = getCallingMethod();
        Call method = controllers.routes.ApiController.apiCall(methodName);
        String url = method.absoluteURL(false, "localhost:9000");
        WSRequest request = ws.url(url);
        request.setContentType("application/json");
        request.setBody(Json.toJson(param));
        request.setMethod(method.method());
        return request.execute().thenApply(WSResponse::asJson).thenApply(returnJson -> Json.fromJson(returnJson, returnType));
    }

    protected String getCallingMethod() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

}

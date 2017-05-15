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
import play.mvc.Http;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;


public class ServerConnector implements ServerInterface {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Application app = new GuiceApplicationLoader().builder(ApplicationLoader.Context.create(Environment.simple())).build();
        Injector injector = app.injector();
        System.out.println(injector.instanceOf(ServerConnector.class).helloWorld("Erde").toCompletableFuture().get());
        app.getWrappedApplication().stop();
    }

    private final WSClient ws;
    private final ServerAuthentication serverAuthentication;

    @Inject
    public ServerConnector(WSClient ws, ServerAuthentication serverAuthentication) {
        this.ws = ws;
        this.serverAuthentication = serverAuthentication;
    }

    @Override
    public CompletionStage<String> helloWorld(String name) {
        return apiCall(String.class, name);
    }

    @Override
    public CompletionStage<User> createUser(String mail, char[] password) {
        return apiCall(User.class, mail, password);
    }

    /**
     * @return ob das Login erfolgreich war
     */
    @SuppressWarnings("deprecation")
    public CompletionStage<Boolean> loginAndRemember(String mail, char[] password) {
        return login(mail, password).thenApply(Objects::nonNull);
    }

    @Override
    @Deprecated // existiert nur als Schnittstelle zwischen Client und Server, besser loginAndRemember() verwenden!
    @SuppressWarnings("DeprecatedIsStillUsed")
    public CompletionStage<String> login(String mail, char[] password) {
        return apiCall(String.class, mail, password).thenApply(token -> {
            serverAuthentication.setAuthenticationToken(token);
            return token;
        });
    }

    @Override
    public CompletionStage<Void> logout() {
        CompletionStage<Void> call = apiCall(Void.class);
        serverAuthentication.logout();
        return call;
    }

    @Override
    public CompletionStage<User> getCurrentUser() {
        return apiCall(User.class);
    }

    @Override
    public CompletionStage<Void> changeUserPassword(char[] newPassword) {
        return apiCall(Void.class, (Object) newPassword);
    }

    private <R> CompletionStage<R> apiCall(Class<R> returnType, Object... param) {
        String methodName = getCallingMethod();
        Call method = controllers.routes.ApiController.apiCall((String) methodName);
        String url = method.absoluteURL(false, "localhost:9000");
        WSRequest request = ws.url(url);
        request.setContentType("application/json");
        serverAuthentication.getAuthenticationToken().ifPresent(request::setAuth);
        request.setBody(Json.toJson(param));
        request.setMethod(method.method());
        return request.execute().thenApply(
                wsResponse -> wsResponse.getStatus() == Http.Status.NO_CONTENT ? null : wsResponse.asJson()
        ).thenApply(
                returnJson -> returnJson == null ? null : Json.fromJson(returnJson, returnType)
        );
    }

    protected String getCallingMethod() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

}

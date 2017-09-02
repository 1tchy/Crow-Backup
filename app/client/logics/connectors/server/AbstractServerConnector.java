package client.logics.connectors.server;

import controllers.routes;
import play.api.mvc.Call;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Http;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;

public class AbstractServerConnector {

    private final WSClient ws;
    private final ServerAuthentication serverAuthentication;

    public AbstractServerConnector(WSClient ws, ServerAuthentication serverAuthentication) {
        this.ws = ws;
        this.serverAuthentication = serverAuthentication;
    }

    protected <R> CompletionStage<R> apiCall(Class<R> returnType, Object... param) {
        Method method = getCallingMethod();
        Call call = routes.BaseApiController.apiCall(method.getDeclaringClass().getInterfaces()[0].getSimpleName(), method.getName());
        String url = call.absoluteURL(false, "localhost:9000");
        WSRequest request = ws.url(url);
        request.setContentType("application/json");
        serverAuthentication.getAuthenticationToken().ifPresent(request::setAuth);
        request.setBody(Json.toJson(param));
        request.setMethod(call.method());
        request.addHeader("Csrf-Token", "nocheck");
        return request.execute().thenApply(
            wsResponse -> {
                switch (wsResponse.getStatus()) {
                    case Http.Status.NO_CONTENT:
                        return null;
                    case Http.Status.NOT_FOUND:
                        throw new RuntimeException(method + " not found under " + url);
                    case Http.Status.FORBIDDEN:
                        throw new RuntimeException("not allowed to call " + method + "; are you logged in?");
                    default:
                        return wsResponse.asJson();
                }
            }
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

package controllers.actions;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import services.LoginTokenService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Führt eine Action/Methode aus und garantiert dabei, dass ein Benutzer eingeloggt ist.
 * Falls das nicht so ist, wird eine Fehler-Seite zurück gegeben.
 */
public class WithUserAction extends Action.Simple implements ExplicitAction {

    private final LoginTokenService loginTokenService;

    @Inject
    public WithUserAction(LoginTokenService loginTokenService) {
        this.loginTokenService = loginTokenService;
    }

    /**
     * @see controllers.actions.WithUserAction
     */
    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        return call(ctx, delegate::call);
    }

    /**
     * @see controllers.actions.WithUserAction
     */
    @Override
    public CompletionStage<Result> call(Http.Context ctx, Function<Http.Context, CompletionStage<Result>> callable) {
        String authentication = ctx.request().getHeader("Authentication");
        if (authentication == null) {
            return forbiddenPromise("Authentication-Header fehlt");
        }
        if (!authentication.startsWith(LoginTokenService.AUTHENTICATION_TYPE)) {
            return forbiddenPromise("Authentication-Type unbekannt: " + authentication);
        }
        return loginTokenService.unpack(authentication.replaceFirst(LoginTokenService.AUTHENTICATION_TYPE + "\\s+", "")).thenCompose(user -> {
            if (!user.isPresent()) {
                return forbiddenPromise("Authentication-User unbekannt");
            }
            AuthenticatedRequest authenticatedRequest = new AuthenticatedRequest(ctx.request(), user.get());
            Http.Context authenticatedContext = ctx.withRequest(authenticatedRequest);
            return callable.apply(authenticatedContext);
        });
    }

    private CompletableFuture<Result> forbiddenPromise(String message) {
        return CompletableFuture.completedFuture(forbidden(message));
    }
}

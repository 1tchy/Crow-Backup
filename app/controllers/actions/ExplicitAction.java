package controllers.actions;

import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * @see controllers.actions.WithExplicitAction
 */
public interface ExplicitAction {
    CompletionStage<Result> call(Http.Context ctx, Function<Http.Context, CompletionStage<Result>> callable);
}

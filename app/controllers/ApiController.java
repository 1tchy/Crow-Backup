package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import models.ServerInterface;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ApiController extends Controller implements ServerInterface {

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> apiCall(String methodName) {
        try {
            JsonNode json = request().body().asJson();
            final Method method = getMethod(methodName);
            final Object parameter;
            try {
                parameter = Json.mapper().treeToValue(json, method.getParameterTypes()[0]);
            } catch (JsonProcessingException e) {
                return CompletableFuture.completedFuture(badRequest(json + " ist ungültig für " + methodName));
            }
            try {
                @SuppressWarnings("unchecked") CompletionStage<?> result = (CompletionStage<?>) method.invoke(this, parameter);
                return result.thenApplyAsync(resultJsonObject -> ok(Json.toJson(resultJsonObject)));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return CompletableFuture.completedFuture(badRequest("Fehler QJRgagH7XSUwuAPn"));
            }
        } catch (NoSuchMethodException e) {
            return CompletableFuture.completedFuture(notFound(methodName));
        }
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
        return CompletableFuture.completedFuture("Hallo " + name);
    }
}

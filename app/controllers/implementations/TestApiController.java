package controllers.implementations;

import models.interfaces.TestServerInterface;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class TestApiController implements TestServerInterface {

    @Override
    public CompletionStage<String> helloWorld(String name) {
        return CompletableFuture.completedFuture(name == null ? null : "Hallo " + name);
    }

}

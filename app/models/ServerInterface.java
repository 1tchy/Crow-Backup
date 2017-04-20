package models;

import java.util.concurrent.CompletionStage;

public interface ServerInterface {

    CompletionStage<String> helloWorld(String name);

}

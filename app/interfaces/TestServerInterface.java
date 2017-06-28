package interfaces;

import java.util.concurrent.CompletionStage;

public interface TestServerInterface {

    CompletionStage<String> helloWorld(String name);

}

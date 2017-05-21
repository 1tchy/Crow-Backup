package client.logic.connectors.server;

import models.interfaces.TestServerInterface;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;


public class TestServerConnector extends AbstractServerConnector implements TestServerInterface {

    @Inject
    public TestServerConnector(WSClient ws, ServerAuthentication serverAuthentication) {
        super(ws, serverAuthentication);
    }

    @Override
    public CompletionStage<String> helloWorld(String name) {
        return apiCall(String.class, name);
    }

}

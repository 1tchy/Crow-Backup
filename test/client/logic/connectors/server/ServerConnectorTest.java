package client.logic.connectors.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerConnectorTest {

    @Mock
    private
    WSClient ws;
    @Mock
    private
    WSRequest request;
    @Mock
    private WSResponse response;
    private String jsonResponseContent;
    @InjectMocks
    private ServerConnector cut;

    @Before
    public void setup() {
        when(ws.url(anyString())).thenReturn(request);
        when(request.execute()).thenReturn(CompletableFuture.completedFuture(response));
        when(response.asJson()).thenAnswer(invocation -> Json.parse(jsonResponseContent));
    }

    @Test
    public void test_getCallerClassName() {
        //Arrange
        //Act
        final String actual = delegateCallingMethod();
        //Assert
        assertEquals("test_getCallerClassName", actual);
    }

    private String delegateCallingMethod() {
        return cut.getCallingMethod();
    }

    @Test
    public void test_simpleMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        jsonResponseContent = "\"Hallo Welt\"";
        //Act
        CompletionStage<String> actualPromise = cut.helloWorld("Welt");
        //Assert
        String actual = actualPromise.toCompletableFuture().get();
        assertEquals("Hallo Welt", actual);
    }

}
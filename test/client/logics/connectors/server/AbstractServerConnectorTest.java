package client.logics.connectors.server;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Spy;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AbstractServerConnectorTest {
    protected String jsonResponseContent;
    @Mock
    private WSClient ws;
    @Mock
    protected WSRequest request;
    @Mock
    private WSResponse response;
    @SuppressWarnings("unused")//is injected by InjectMocks
    @Spy
    private ServerAuthentication serverAuthentication = new ServerAuthentication();

    @Before
    public void setup() {
        when(ws.url(anyString())).thenReturn(request);
        when(request.execute()).thenReturn(CompletableFuture.completedFuture(response));
        when(response.asJson()).thenAnswer(invocation -> jsonResponseContent == null ? null : Json.parse(jsonResponseContent));
    }
}

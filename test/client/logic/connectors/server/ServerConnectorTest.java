package client.logic.connectors.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
    @SuppressWarnings("unused")//is injected by InjectMocks
    @Spy
    private ServerAuthentication serverAuthentication = new ServerAuthentication();
    private String jsonResponseContent;
    @InjectMocks
    private ServerConnector cut;

    @Before
    public void setup() {
        when(ws.url(anyString())).thenReturn(request);
        when(request.execute()).thenReturn(CompletableFuture.completedFuture(response));
        when(response.asJson()).thenAnswer(invocation -> jsonResponseContent == null ? null : Json.parse(jsonResponseContent));
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

    @Test
    public void test_when_login_itIsRemembered() {
        //Arrange
        String token = "Token asldklasdflfladfda";
        jsonResponseContent = "\"" + token + "\"";
        cut.loginAndRemember("test@example.com", "1234".toCharArray());
        verify(request, never()).setAuth(anyString());
        //Act
        cut.getCurrentUser();
        //Assert
        verify(request).setAuth(token);
    }

    @Test
    public void test_when_logout_itIsStillDoneAuthenticated() {
        //Arrange
        test_when_login_itIsRemembered();
        Mockito.clearInvocations(request);
        //Act
        cut.logout();
        //Assert
        verify(request).setAuth(anyString());
    }

    @Test
    public void test_when_logout_userIsNoMoreLoggedIn() {
        //Arrange
        test_when_logout_itIsStillDoneAuthenticated();
        Mockito.clearInvocations(request);
        //Act
        cut.getCurrentUser();
        //Assert
        verify(request, never()).setAuth(anyString());
    }

    @Test
    public void test_changeUserPassword_works_when_authenticated() throws ExecutionException, InterruptedException {
        //Arrange
        test_when_login_itIsRemembered();
        Mockito.clearInvocations(request);
        jsonResponseContent = null;
        //Act
        CompletionStage<Void> actual = cut.changeUserPassword("5678".toCharArray());
        //Assert
        verify(request).setAuth(anyString());
        assertNull(actual.toCompletableFuture().get());
    }

}
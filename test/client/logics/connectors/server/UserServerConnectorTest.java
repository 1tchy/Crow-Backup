package client.logics.connectors.server;

import client.logics.connectors.server.implementations.UserServerConnector;
import controllers.actions.WithUserAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserServerConnectorTest extends AbstractServerConnectorTest {

    @InjectMocks
    private UserServerConnector cut;

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
        verify(request).addHeader(WithUserAction.AUTHENTICATION_HEADER_NAME, token);
    }

    @Test
    public void test_when_logout_itIsStillDoneAuthenticated() {
        //Arrange
        test_when_login_itIsRemembered();
        Mockito.clearInvocations(request);
        //Act
        cut.logout();
        //Assert
        verify(request).addHeader(eq(WithUserAction.AUTHENTICATION_HEADER_NAME), anyString());
    }

    @Test
    public void test_when_logout_userIsNoMoreLoggedIn() {
        //Arrange
        test_when_logout_itIsStillDoneAuthenticated();
        Mockito.clearInvocations(request);
        //Act
        cut.getCurrentUser();
        //Assert
        verify(request, never()).addHeader(eq(WithUserAction.AUTHENTICATION_HEADER_NAME), anyString());
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
        verify(request).addHeader(eq(WithUserAction.AUTHENTICATION_HEADER_NAME), anyString());
        assertNull(actual.toCompletableFuture().get());
    }

}

package client.views;

import client.logics.AbstractFXTest;
import client.logics.connectors.server.implementations.UserServerConnector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import models.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class SignUpDialogTest extends AbstractFXTest {

    private static final String EXAMPLE_MAIL = "test@test.com";
    private static final char[] EXAMPLE_PW = "pw".toCharArray();
    @Mock
    private UserServerConnector userServerConnector;
    @InjectMocks
    private SignUpDialog cut;
    private final SignUpDialogPage page = new SignUpDialogPage(this);

    @NotNull
    @Override
    protected Class<? extends Application> getAppClass() {
        return SignUpDialogPage.CreateUserDialogApplicationWrapper.class;
    }

    @Before
    public void setup() throws Exception {
        Platform.runLater(() -> {
            MockitoAnnotations.initMocks(SignUpDialogTest.this);
            SignUpDialogPage.CreateUserDialogApplicationWrapper.setSignUpDialog(cut);
            User user = new User(EXAMPLE_MAIL, null);
            when(userServerConnector.createUser(EXAMPLE_MAIL, EXAMPLE_PW)).thenReturn(CompletableFuture.completedFuture(user));
        });
        super.setup();
    }

    @Test
    public void test_createUser_when_happyFlow() {
        // Arrange
        //Act 1
        page.fillCreateUser(EXAMPLE_MAIL, EXAMPLE_PW);
        //Assert 1
        page.verifyCreateUserButtonIsEnabled();
        verifyThat(((Stage) window(0)).getTitle(), equalTo("new user"));
        //Act 2
        SignUpDialogPage p = page.performCreateUser();
        //Assert 2
        verify(userServerConnector).createUser(eq(EXAMPLE_MAIL), any(char[].class)); // password is already cleared at this point
    }


    @Test
    public void test_createUser_when_passwordsDiffer() {
        //Arrange
        // Act
        page.fillCreateUser(EXAMPLE_MAIL, "pw1", "pw2");
        //Assert
        page.verifyCreateUserButtonIsDisabled();
    }

    @Test
    public void test_createUser_when_emptyPassword() {
        //Arrange
        // Act
        page.fillCreateUser(EXAMPLE_MAIL, new char[]{});
        //Assert
        page.verifyCreateUserButtonIsDisabled();
    }

    @Test
    public void test_createUser_when_emptyMail() {
        //Arrange
        // Act
        page.fillCreateUser("", EXAMPLE_PW);
        //Assert
        page.verifyCreateUserButtonIsDisabled();
    }

}

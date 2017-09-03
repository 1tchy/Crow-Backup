package client.views;

import client.logics.AbstractFXTest;
import client.logics.connectors.server.implementations.FriendshipServerConnector;
import client.viewmodels.AddFriendDialogController;
import javafx.stage.Stage;
import models.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class AddFriendDialogTest extends AbstractFXTest {

    private static final String EXAMPLE_FRIENDS_MAIL = "test@test.com";
    @Mock
    private FriendshipServerConnector friendshipServerConnector;
    @InjectMocks
    private AddFriendDialogController cut;
    private final AddFriendDialogPage page = new AddFriendDialogPage(this);
    private User user;

    @Before
    public void setup() throws Exception {
        user = new User(EXAMPLE_FRIENDS_MAIL, null);
        when(friendshipServerConnector.findFriend(anyString())).thenReturn(CompletableFuture.completedFuture(null));
        when(friendshipServerConnector.findFriend(EXAMPLE_FRIENDS_MAIL)).thenReturn(CompletableFuture.completedFuture(user));
    }

    @Override
    public void doStart(Stage stage) {
        cut.show();
    }

    @Test
    public void test_addFriend_when_happyFlow() {
        // Arrange
        //Act 1
        page.fillFriendsMail(EXAMPLE_FRIENDS_MAIL);
        //Assert 1
        verifyThat(((Stage) window(0)).getTitle(), equalTo("Freund hinzufügen"));
        page.verifyFriendRequestButtonIsEnabled();
        page.verifyUnknownMailLabelHidden();
        //Act 2
        page.sendFriendRequest();
        //Assert 2
        verify(friendshipServerConnector).addFriend(user);
    }


    @Test
    public void test_addFriend_when_invalidMail() {
        //Arrange
        // Act
        page.fillFriendsMail("abc");
        //Assert
        page.verifyFriendRequestButtonIsDisabled();
        page.verifyUnknownMailLabelHidden();
    }

    @Test
    public void test_addFriend_when_unknownMail() {
        //Arrange
        // Act
        page.fillFriendsMail("unknown@example.com");
        //Assert
        page.verifyFriendRequestButtonIsDisabled();
        page.verifyUnknownMailLabelVisible();
    }

}

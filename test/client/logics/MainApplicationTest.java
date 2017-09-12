package client.logics;

import client.logics.connectors.server.implementations.FriendshipServerConnector;
import client.logics.connectors.server.implementations.UserServerConnector;
import client.logics.queue.QueueAccessor;
import client.viewmodels.EventHandlingController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import helpers.GeneralHelpers;
import javafx.application.Platform;
import javafx.stage.Stage;
import models.user.FriendLink;
import models.user.Friendship;
import models.user.User;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.testfx.matcher.control.ListViewMatchers;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

public class MainApplicationTest extends AbstractFXTest {

    @InjectMocks
    private MainApplication cut;
    private UserServerConnector userServerConnector;
    private FriendshipServerConnector friendshipServerConnector;
    private Stage stage;
    private User user;

    @Override
    public void doStart(Stage stage) {
        ClientModule clientModule = new ClientModule() {
            @Override
            protected void configure() {
                super.configure();
                userServerConnector = mock(UserServerConnector.class, Answers.RETURNS_DEEP_STUBS);
                user = new User("a@a.ch", "");
                when(userServerConnector.getCurrentUser()).thenReturn(CompletableFuture.completedFuture(user));
                friendshipServerConnector = mock(FriendshipServerConnector.class);
                when(friendshipServerConnector.openRequests()).thenReturn(CompletableFuture.completedFuture(new Friendship[]{}));
                when(friendshipServerConnector.list()).thenReturn(CompletableFuture.completedFuture(new Friendship[]{}));
                EventHandlingController eventHandlingController = new EventHandlingController(userServerConnector, friendshipServerConnector, null, null, new QueueAccessor());
                eventHandlingController.refreshCurrentUser();
                bind(EventHandlingController.class).toInstance(eventHandlingController);
            }
        };
        Injector injector = Guice.createInjector(clientModule);
        cut = new MainApplication(injector);
        this.stage = stage;
    }

    @Test
    public void test_hauptseite_when_appGestartet() {
        // Arrange
        //Act
        startAndWait();
        //Assert
        Stage window = (Stage) window(0);
        verifyThat(window.getTitle(), equalTo("Crow Backup"));
        verifyThat("#button_Login", hasText("Anmelden"));
        verifyThat("#button_Settings", hasText("Einstellungen"));
    }

    @Test
    public void test_hauptseite_when_oneFriendRequestOpen() throws InterruptedException {
        //Arrange
        Friendship friendship = new Friendship(new FriendLink(user, new User("b@b.ch", "")));
        when(friendshipServerConnector.openRequests()).thenReturn(CompletableFuture.completedFuture(new Friendship[]{friendship}));
        //Act
        startAndWait();
        //Assert
        GeneralHelpers.awaitNoAssertionError(() -> verifyThat("#listView_MyFriends", ListViewMatchers.hasListCell(friendship)));
    }

    @Test
    public void test_hauptseite_when_oneFriendship() throws InterruptedException {
        //Arrange
        Friendship friendship = new Friendship(new FriendLink(user, new User("b@b.ch", "")));
        when(friendshipServerConnector.list()).thenReturn(CompletableFuture.completedFuture(new Friendship[]{friendship}));
        //Act
        startAndWait();
        //Assert
        GeneralHelpers.awaitNoAssertionError(() -> verifyThat("#listView_MyFriends", ListViewMatchers.hasListCell(friendship)));
    }

    private void startAndWait() {
        Platform.runLater(() -> cut.start(this.stage));
        while (listWindows().isEmpty()) {
            Thread.yield();
        }
    }

}

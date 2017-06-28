package client.logics.connectors.server;

import client.logics.connectors.server.implementations.FriendshipServerConnector;
import models.user.Friendship;
import models.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import play.libs.Json;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipServerConnectorTest extends AbstractServerConnectorTest {

    @InjectMocks
    private FriendshipServerConnector cut;

    @Test
    public void test_create() throws ExecutionException, InterruptedException {
        //Arrange
        User user = new User();
        user.setMail("test@example.com");
        //Act
        CompletionStage<Void> actualPromise = cut.addFriend(user);
        //Assert
        verify(request).setBody(eq(Json.toJson(new User[]{user})));
        Void actual = actualPromise.toCompletableFuture().get();
        assertNull(actual);
    }

    @Test
    public void test_openRequests() throws ExecutionException, InterruptedException {
        //Arrange
        jsonResponseContent = "[{\"links\": [{}]}]";
        //Act
        CompletionStage<Friendship[]> actualPromise = cut.openRequests();
        //Assert
        verify(request).setBody(eq(Json.toJson(new Object[0])));
        Friendship[] actual = actualPromise.toCompletableFuture().get();
        assertEquals(1, actual.length);
        assertEquals(1, actual[0].getLinks().size());
    }

    @Test
    public void test_list() throws ExecutionException, InterruptedException {
        //Arrange
        jsonResponseContent = "[{\"links\": [{}]}]";
        //Act
        CompletionStage<Friendship[]> actualPromise = cut.list();
        //Assert
        verify(request).setBody(eq(Json.toJson(new Object[0])));
        Friendship[] actual = actualPromise.toCompletableFuture().get();
        assertEquals(1, actual.length);
        assertEquals(1, actual[0].getLinks().size());
    }

    @Test
    public void test_remove() throws ExecutionException, InterruptedException {
        //Arrange
        User user = new User();
        user.setMail("test@example.com");
        //Act
        CompletionStage<Void> actualPromise = cut.deleteFriend(user);
        //Assert
        verify(request).setBody(eq(Json.toJson(new User[]{user})));
        Void actual = actualPromise.toCompletableFuture().get();
        assertNull(actual);
    }

}
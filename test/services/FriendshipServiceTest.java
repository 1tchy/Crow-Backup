package services;

import helpers.WithTransaction;
import models.user.FriendLink;
import models.user.Friendship;
import models.user.User;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class FriendshipServiceTest extends WithTransaction {

    private FriendshipService cut;
    private User userFrom;
    private User userTo;

    @Before
    public void setup() {
        cut = new FriendshipService(persistenceService);
        userFrom = new User();
        userTo = new User();
        persistenceService.persist(userFrom, userTo);
    }

    @Test
    public void test_createNewFriendship() throws ExecutionException, InterruptedException {
        //Arrange
        assertEquals(0, cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get().length);
        //Act
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        //Assert
        Friendship[] friendships = cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get();
        assertEquals(1, friendships.length);
        Friendship friendship = friendships[0];
        assertFalse(friendship.isConfirmed());
        assertThat(friendship.getLinks(), hasSize(1));
        FriendLink friendLink = friendship.getLinks().iterator().next();
        assertEquals(userFrom, friendLink.getFrom());
        assertEquals(userTo, friendLink.getTo());
    }

    @Test
    public void test_createNewFriendship_when_thereIsAlreadyOne() throws ExecutionException, InterruptedException {
        //Arrange
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        assertEquals(1, cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get().length);
        //Act
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        //Assert
        assertEquals(1, cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get().length);
    }

    @Test
    public void test_createNewFriendship_when_thereIsARequest() throws ExecutionException, InterruptedException {
        //Arrange
        cut.createFriendshipWithTransaction(userTo, userFrom.getId()).toCompletableFuture().get();
        //Act
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        //Assert
        Friendship[] openRequests = cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get();
        assertEquals(0, openRequests.length);
        Friendship[] currentFriendships = cut.listForUserWithTransaction(userTo, true).toCompletableFuture().get();
        assertEquals(1, currentFriendships.length);
        Friendship friendship = currentFriendships[0];
        assertTrue(friendship.isConfirmed());
        assertThat(friendship.getLinks(), containsInAnyOrder(Arrays.asList(
                Matchers.<FriendLink>allOf(hasProperty("from", equalTo(userFrom)), hasProperty("to", equalTo(userTo))), //request
                Matchers.<FriendLink>allOf(hasProperty("to", equalTo(userFrom)), hasProperty("from", equalTo(userTo))) //confirmation
        )));
    }

    @Test
    public void test_createNewFriendship_when_thereIsACompleteFriendship() throws ExecutionException, InterruptedException {
        //Arrange
        cut.createFriendshipWithTransaction(userTo, userFrom.getId()).toCompletableFuture().get();
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        //Act
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        //Assert
        Friendship[] openRequests = cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get();
        assertEquals(0, openRequests.length);
        Friendship[] currentFriendships = cut.listForUserWithTransaction(userTo, true).toCompletableFuture().get();
        assertEquals(1, currentFriendships.length);
    }

    @Test
    public void test_findOpenFriendships() throws ExecutionException, InterruptedException {
        //Arrange
        cut.createFriendshipWithTransaction(userFrom, userTo.getId()).toCompletableFuture().get();
        //Act
        Friendship[] actual = cut.listForUserWithTransaction(userTo, false).toCompletableFuture().get();
        //Assert
        assertEquals(1, actual.length);
        assertFalse(actual[0].isConfirmed());
        FriendLink link = actual[0].getLinks().iterator().next();
        assertEquals(link.getFrom(), userFrom);
        assertEquals(link.getTo(), userTo);
    }

}
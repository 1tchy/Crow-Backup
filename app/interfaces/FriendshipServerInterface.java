package interfaces;

import models.user.Friendship;
import models.user.User;

import java.util.concurrent.CompletionStage;

public interface FriendshipServerInterface {

    CompletionStage<User> findFriend(String mail);

    CompletionStage<Void> addFriend(User user);

    CompletionStage<Friendship[]> list();

    CompletionStage<Friendship[]> openRequests();

    CompletionStage<Void> deleteFriend(User user);

}

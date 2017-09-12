package client.logics.connectors.server.implementations;

import client.logics.connectors.server.AbstractServerConnector;
import client.logics.connectors.server.ServerAuthentication;
import interfaces.FriendshipServerInterface;
import models.user.Friendship;
import models.user.User;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;


public class FriendshipServerConnector extends AbstractServerConnector implements FriendshipServerInterface {

    @Inject
    public FriendshipServerConnector(WSClient ws, ServerAuthentication serverAuthentication) {
        super(ws, serverAuthentication);
    }

    @Override
    public CompletionStage<User> findFriend(String mail) {
        return apiCall(User.class, mail);
    }

    @Override
    public CompletionStage<Void> addFriend(User user) {
        return apiCall(Void.class, user);
    }

    @Override
    public CompletionStage<Friendship[]> list() {
        return apiCall(Friendship[].class);
    }

    @Override
    public CompletionStage<Friendship[]> openRequests() {
        return apiCall(Friendship[].class);
    }

    @Override
    public CompletionStage<Void> deleteFriend(User user) {
        return apiCall(Void.class, user);
    }

}

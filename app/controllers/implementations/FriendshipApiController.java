package controllers.implementations;

import controllers.actions.AuthenticatedRequest;
import controllers.actions.WithUser;
import models.interfaces.FriendshipServerInterface;
import models.user.Friendship;
import models.user.User;
import play.mvc.Controller;
import services.FriendshipService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class FriendshipApiController implements FriendshipServerInterface {

    private final FriendshipService friendshipService;

    @Inject
    public FriendshipApiController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @Override
    @WithUser
    public CompletionStage<Void> addFriend(User user) {
        AuthenticatedRequest request = (AuthenticatedRequest) Controller.request();
        return friendshipService.createFriendshipWithTransaction(request.getAuthenticatedUser(), user.getId());
    }

    @Override
    @WithUser
    public CompletionStage<Friendship[]> list() {
        AuthenticatedRequest request = (AuthenticatedRequest) Controller.request();
        return friendshipService.listForUserWithTransaction(request.getAuthenticatedUser(), true);
    }

    @Override
    @WithUser
    public CompletionStage<Friendship[]> openRequests() {
        AuthenticatedRequest request = (AuthenticatedRequest) Controller.request();
        return friendshipService.listForUserWithTransaction(request.getAuthenticatedUser(), false);
    }

    @Override
    @WithUser
    public CompletionStage<Void> deleteFriend(User user) {
        AuthenticatedRequest request = (AuthenticatedRequest) Controller.request();
        return friendshipService.removeWithTransaction(request.getAuthenticatedUser(), user.getId());
    }
}

package services;

import models.user.FriendLink;
import models.user.Friendship;
import models.user.User;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class FriendshipService {

    private final PersistenceService persistenceService;

    @Inject
    public FriendshipService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public CompletionStage<User> findFriend(String mail) {
        return persistenceService.asyncWithTransaction(true, () -> persistenceService.readOne(User.class, "SELECT u FROM User u WHERE u.mail = ?1", mail).orElse(null));
    }

    public CompletionStage<Void> createFriendshipWithTransaction(User fromUser, long toUserId) {
        return persistenceService.asyncWithTransaction(() -> {
            User toUser = persistenceService.readUnique(User.class, toUserId);
            createFriendship(fromUser, toUser);
        });
    }

    private void createFriendship(User fromUser, User toUser) {
        Optional<Friendship> existingFriendship = searchFriendship(fromUser, toUser);
        Friendship friendship;
        if (existingFriendship.isPresent()) {
            friendship = existingFriendship.get();
            for (FriendLink existingLink : friendship.getLinks()) {
                if (existingLink.getFrom().getId() == fromUser.getId()) {
                    return;
                }
            }
        } else {
            friendship = new Friendship();
        }
        FriendLink friendLink = new FriendLink(fromUser, toUser);
        try {
            friendship.addLink(friendLink);
        } catch (Friendship.TooManyLinksException e) {
            throw new RuntimeException("This should not be possiple", e);
        }
        persistenceService.persist(friendLink, friendship);
    }

    private Optional<Friendship> searchFriendship(User oneUser, User anotherUser) {
        return persistenceService.readOne(Friendship.class, "SELECT f FROM Friendship AS f JOIN f.links AS l WHERE (l.from = ?1 AND l.to = ?2) OR (l.from = ?2 AND l.to = ?1)", oneUser, anotherUser);
    }

    public CompletionStage<Friendship[]> listForUserWithTransaction(User user, boolean confirmed) {
        return persistenceService.asyncWithTransaction(false, () -> {
            String query = "SELECT f FROM Friendship AS f JOIN f.links AS l WHERE l.to = ?1 AND SIZE(f.links) = " + (confirmed ? 2 : 1);
            return persistenceService.read(Friendship.class, query, null, user).stream().peek(friendship -> {
                friendship.getLinks().size();//eager loading links
            }).toArray(Friendship[]::new);
        });
    }

    public CompletionStage<Void> removeWithTransaction(User fromUser, long toUserId) {
        return persistenceService.asyncWithTransaction(() -> {
            User toUser = persistenceService.readUnique(User.class, toUserId);
            removeFriendship(fromUser, toUser);
        });
    }

    private void removeFriendship(User fromUser, User toUser) {
        Optional<Friendship> friendship = searchFriendship(fromUser, toUser);
        if (friendship.isPresent()) {
            for (FriendLink friendLink : friendship.get().getLinks()) {
                persistenceService.remove(friendLink);
            }
            persistenceService.remove(friendship.get());
        }
    }
}

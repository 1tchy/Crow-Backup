package models.user;

import models.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Friendship extends BaseEntity {

    @OneToMany
    private Set<FriendLink> links = new HashSet<>();

    public Set<FriendLink> getLinks() {
        return links;
    }

    public Friendship() {
    }

    public Friendship(FriendLink link) {
        links.add(link);
    }

    public Friendship(FriendLink link1, FriendLink link2) {
        links.add(link1);
        links.add(link2);
    }

    public boolean isConfirmed() {
        return links.size() == 2;
    }

    public void addLink(FriendLink link) throws TooManyLinksException {
        if (links.size() >= 2) {
            throw new TooManyLinksException("A friendship should only be made of maximum 2 links and there are already " + links.size());
        }
        links.add(link);
    }

    public static class TooManyLinksException extends Exception {
        public TooManyLinksException(String message) {
            super(message);
        }

    }
}

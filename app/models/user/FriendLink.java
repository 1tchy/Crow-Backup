package models.user;

import models.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FriendLink extends BaseEntity {

    @ManyToOne
    private User from;
    @ManyToOne
    private User to;

    @SuppressWarnings("unused") //wird für Json-Deserialisierung verwendet
    @Deprecated
    private FriendLink() {
    }

    public FriendLink(User from, User to) {
        this.from = from;
        this.to = to;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

}

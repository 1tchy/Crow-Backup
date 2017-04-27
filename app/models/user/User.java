package models.user;

import models.BaseEntity;

import javax.persistence.Entity;

@Entity
public class User extends BaseEntity {

    private String mail;
    private String passwordHash;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

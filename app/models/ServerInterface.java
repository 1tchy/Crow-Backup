package models;

import models.user.User;

import java.util.concurrent.CompletionStage;

public interface ServerInterface {

    CompletionStage<String> helloWorld(String name);

    CompletionStage<User> createUser(String mail, char[] password);

    CompletionStage<String> login(String mail, char[] password);

    CompletionStage<Void> logout();

    CompletionStage<User> getCurrentUser();

    CompletionStage<Void> changeUserPassword(char[] newPassword);

}

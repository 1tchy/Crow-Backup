package interfaces;

import models.user.User;

import java.util.concurrent.CompletionStage;

public interface UserServerInterface {

    CompletionStage<User> createUser(String mail, char[] password);

    CompletionStage<String> login(String mail, char[] password);

    CompletionStage<Void> logout();

    CompletionStage<User> getCurrentUser();

    CompletionStage<Void> changeUserPassword(char[] newPassword);

}

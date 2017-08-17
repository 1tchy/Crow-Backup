package client.views;

import client.logics.connectors.server.implementations.UserServerConnector;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import models.user.User;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CreateUserDialog extends DialogBase<User> {
    private User user;
    ButtonType createUserButton;

    @Inject
    private UserServerConnector userServerConnector;

    public CreateUserDialog() {
        super();
        super.setHeader("Create a new user");
        super.setTitle("new user");

        setButtons();
        setInput();
        user = new User();

        super.setResultConverter(dialogButton -> {
            if (dialogButton == createUserButton) {
                try {
                    user = userServerConnector.createUser(user.getMail(), user.getPasswordHash().toCharArray()).toCompletableFuture().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return user;
            }
            return null;
        });
    }

    private void setButtons() {
        createUserButton = new ButtonType("Create user", ButtonBar.ButtonData.OK_DONE);
        super.getDialogPane().getButtonTypes().addAll(createUserButton, ButtonType.CANCEL);
    }

    private void setInput() {
        GridPane grid = new GridPane();

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField passwordA = new PasswordField();
        PasswordField passwordB = new PasswordField();
        passwordA.setPromptText("Password");
        passwordB.setPromptText("Repeated password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordA, 1, 1);
        grid.add(new Label("Repeated password:"), 0, 2);
        grid.add(passwordB, 1, 2);
        Node loginNode = super.getDialogPane().lookupButton(createUserButton);
        loginNode.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, passwordA.getText(), passwordB.getText())
                && !isSame(passwordA.getText(), passwordB.getText()));
            user.setMail(newValue);
        });

        passwordA.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, passwordB.getText(), username.getText())
                && !isSame(passwordB.getText(), newValue));
            user.setPasswordHash(newValue);
        });

        passwordB.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, passwordA.getText(), username.getText())
                && !isSame(passwordA.getText(), newValue));
            user.setPasswordHash(newValue);
        });

        super.getDialogPane().setContent(grid);

        Platform.runLater(username::requestFocus);

    }

    private boolean hasText(String... texts) {
        return Arrays.stream(texts).allMatch(s -> s.length() > 0);
    }

    private boolean isSame(String... texts) {
        return !Arrays.stream(texts).allMatch(s -> s.length() > 0)
            && Arrays.stream(texts).distinct().count() == 1;
    }
}

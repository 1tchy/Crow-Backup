package client.views;


import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import models.user.User;

import java.util.Arrays;

public class LoginDialog extends Dialog<User> {

    private User user;

    private ButtonType loginButton;

    public LoginDialog() {
        super();
        setHead();
        setButtons();
        setInput();
        user = new User();

        super.setResultConverter(dialogButton -> {
            if (dialogButton == loginButton) {
                return user;
            }
            return null;
        });
    }

    private void setHead() {
        super.setTitle("Login");
        super.setHeaderText("Look, you can login!");
    }

    private void setButtons() {
        loginButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        super.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);
    }

    private void setInput() {
        GridPane grid = new GridPane();

        TextField username = new TextField("Username");
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        Node loginNode = super.getDialogPane().lookupButton(loginButton);
        loginNode.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, password.getText()));
            user.setMail(newValue);
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, username.getText()));
            user.setPasswordHash(newValue);
        });

        super.getDialogPane().setContent(grid);

        Platform.runLater(username::requestFocus);

    }

    private boolean hasText(String... texts) {
        return Arrays.stream(texts).noneMatch((String s) -> s.trim().isEmpty());
    }
}

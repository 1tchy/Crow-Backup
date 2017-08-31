package client.views;


import client.viewmodels.Login;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.util.Arrays;

public class LoginDialog extends DialogBase<Login> {

    private final CreateUserDialog createUserDialog;
    private Login login;

    private ButtonType loginButton;

    @Inject
    public LoginDialog(CreateUserDialog createUserDialog) {
        super("Look, you chan login!");
        super.setTitle("Login");

        setButtons();
        setInput();
        login = new Login();

        super.setResultConverter(dialogButton -> {
            if (dialogButton == loginButton) {
                return login;
            }
            return null;
        });
        this.createUserDialog = createUserDialog;
    }

    private void setButtons() {
        loginButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        super.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);
    }

    private void setInput() {
        GridPane grid = new GridPane();

        TextField username = new TextField();
        username.setPromptText("Username");
        SafePasswordField password = new SafePasswordField();
        password.setPromptText("Password");
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Pane(), 0, 2);
        Button createUserButton = new Button("Create User");
        createUserButton.setOnAction(e -> createUserDialog.show());
        grid.add(createUserButton, 0, 3);
        Node loginNode = super.getDialogPane().lookupButton(loginButton);
        loginNode.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, password.getText()));
            login.setUser(newValue);
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            loginNode.setDisable(!hasText(newValue, username.getText()));
            login.setPassword(password.getPassword());
        });

        super.getDialogPane().setContent(grid);

        Platform.runLater(username::requestFocus);

    }

    private boolean hasText(String... texts) {
        return Arrays.stream(texts).noneMatch((String s) -> s.trim().isEmpty());
    }
}

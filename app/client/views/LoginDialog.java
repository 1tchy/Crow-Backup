package client.views;


import client.viewmodels.Login;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.util.Arrays;

public class LoginDialog extends DialogBase<Login> {

    private final SignUpDialog signUpDialog;
    private Login login;

    @Inject
    public LoginDialog(SignUpDialog signUpDialog) {
        super("Login", "Look, you chan login!", "Login");

        setInput();
        login = new Login();

        super.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return login;
            }
            return null;
        });
        this.signUpDialog = signUpDialog;
    }

    private void setInput() {
        GridPane grid = new GridPane();

        TextField username = GuiHelper.createTextField("username", "Username");
        SafePasswordField password = GuiHelper.createPasswordField("password", "Password");
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Pane(), 0, 2);
        Button createUserButton = new Button("Create User");
        createUserButton.setOnAction(e -> signUpDialog.show());
        grid.add(createUserButton, 0, 3);
        Node okButton = super.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!hasText(newValue, password.getText()));
            login.setUser(newValue);
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!hasText(newValue, username.getText()));
            login.setPassword(password.getPassword());
        });

        super.getDialogPane().setContent(grid);

        Platform.runLater(username::requestFocus);

    }

    private boolean hasText(String... texts) {
        return Arrays.stream(texts).noneMatch((String s) -> s.trim().isEmpty());
    }
}

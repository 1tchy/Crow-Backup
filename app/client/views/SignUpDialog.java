package client.views;

import client.logics.connectors.server.implementations.UserServerConnector;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import models.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class SignUpDialog extends DialogBase<User> {
    private TextField username;
    private SafePasswordField password1;
    private SafePasswordField password2;

    @Inject
    private UserServerConnector userServerConnector;

    public SignUpDialog() {
        super("Registrieren", "Create a new user", "Create user");

        username = GuiHelper.createTextField("username", "Username");
        password1 = GuiHelper.createPasswordField("password1", "Password");
        password2 = GuiHelper.createPasswordField("password2", "Repeated password");

        getDialogPane().setContent(getContent());

        initButton(getDialogPane());

        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    User result = userServerConnector.createUser(username.getText(), password1.getPassword()).toCompletableFuture().get();
                    password1.clear();
                    password2.clear();
                    return result;
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        Platform.runLater(username::requestFocus);
    }

    @NotNull
    private GridPane getContent() {
        GridPane grid = new GridPane();

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password1, 1, 1);
        grid.add(new Label("Repeated password:"), 0, 2);
        grid.add(password2, 1, 2);

        return grid;
    }

    private void initButton(DialogPane dialogPane) {
        Node okButton = dialogPane.lookupButton(okButtonType);

        okButton.setDisable(true);

        ChangeListener<String> buttonActivator = (observable, oldValue, newValue) -> {
            boolean isOk = GuiHelper.hasText(username.getText(), password1.getText(), password2.getText()) && GuiHelper.isSame(password1.getText(), password2.getText());
            okButton.setDisable(!isOk);
        };
        username.textProperty().addListener(buttonActivator);
        password1.textProperty().addListener(buttonActivator);
        password2.textProperty().addListener(buttonActivator);
    }

}

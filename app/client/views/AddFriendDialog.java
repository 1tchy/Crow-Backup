package client.views;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import models.user.User;
import org.jetbrains.annotations.NotNull;

public class AddFriendDialog extends DialogBase<User> {
    private final Label notFoundLabel;
    private final TextField mail;
    private Node okButton;
    private User currentUser;

    public AddFriendDialog() {
        super("Freund hinzufügen", "Schicken Sie einem Freund eine Freundschaftsanfrage,\num bei ihm Ihr Backup ablegen zu können.", "Anfrage abschicken");

        okButton = getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        notFoundLabel = new Label("Kein Freund mit dieser Mailadresse gefunden.");
        notFoundLabel.setVisible(false);
        mail = GuiHelper.createTextField("mail", "Mailadresse des Freundes");

        getDialogPane().setContent(getContent());

        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return currentUser;
            }
            return null;
        });

        Platform.runLater(mail::requestFocus);
    }

    @NotNull
    private GridPane getContent() {
        GridPane grid = new GridPane();

        grid.add(new Label("Mailadresse:"), 0, 0);
        grid.add(mail, 1, 0);
        grid.add(notFoundLabel, 0, 1, 2, 1);

        return grid;
    }

    public void addMailFieldListener(ChangeListener<String> listener) {
        mail.textProperty().addListener(listener);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setOkButtonDisable(boolean disable) {
        okButton.setDisable(disable);
    }

    public void setCursor(Cursor cursor) {
        getDialogPane().setCursor(cursor);
    }

    public void setNotFoundLabelVisible(boolean visible) {
        notFoundLabel.setVisible(visible);
    }
}

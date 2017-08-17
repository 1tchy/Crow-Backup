package client.views;

import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class DialogBase<T> extends Dialog<T> {
    private Label headerText;

    public DialogBase() {
        super();
        getDialogPane().getStylesheets().add(getClass().getResource("/client/views/stylesheets/DialogStyles.css").toExternalForm());
        getDialogPane().getStylesheets().add(getClass().getResource("/client/views/stylesheets/base.css").toExternalForm());
        headerText = new Label();
        headerText.getStyleClass().add("header");
        getDialogPane().setHeader(headerText);

    }

    public void setHeader(String text) {
        this.headerText.setText(text);
    }
}

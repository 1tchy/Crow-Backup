package client.views;

import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class DialogBase<T> extends Dialog<T> {

    public DialogBase(String headerText) {
        getDialogPane().getStylesheets().add(getClass().getResource("/client/views/stylesheets/DialogStyles.css").toExternalForm());
        getDialogPane().getStylesheets().add(getClass().getResource("/client/views/stylesheets/base.css").toExternalForm());
        Label header = new Label(headerText);
        header.getStyleClass().add("header");
        getDialogPane().setHeader(header);
    }

}

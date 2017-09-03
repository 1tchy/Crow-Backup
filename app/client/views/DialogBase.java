package client.views;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class DialogBase<T> extends Dialog<T> {

    protected final ButtonType okButtonType;

    public DialogBase(String title, String headerText, String okButtonText) {
        setTitle(title);
        getDialogPane().getStylesheets().add(getClass().getResource("/client/views/stylesheets/DialogStyles.css").toExternalForm());
        getDialogPane().getStylesheets().add(getClass().getResource("/client/views/stylesheets/base.css").toExternalForm());
        Label header = new Label(headerText);
        header.getStyleClass().add("header");
        getDialogPane().setHeader(header);
        okButtonType = new ButtonType(okButtonText, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
    }

}

package client.views;

import javafx.application.Application;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public abstract class FXDialogApplicationWrapper<D extends Dialog> extends Application {

    abstract D createDialog();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.hide();
        Dialog cut = createDialog();
        cut.show();
    }
}

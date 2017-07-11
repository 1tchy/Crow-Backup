package client.logics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Crow Backup");

        initRootLayout();

        showOverview();

        showScene();
    }

    private void showScene() {
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        rootLayout = load("/client/views/RootLayout.fxml");
    }

    private <T> T load(String fxmlResource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource(fxmlResource));
        try {
            try {
                return loader.load();
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("Location is not set")) {
                    throw new IOException("Not the location but the resource probably could not be found", e);
                }
                throw e;
            }
        } catch (IOException e) {
            String fullFxmlResourcePath = MainApplication.class.getResource("/").getPath().replaceFirst("/$", "") + fxmlResource;
            throw new RuntimeException(fullFxmlResourcePath + " nicht gefunden", e);
        }
    }

    /**
     * Shows the Crow Backup overview inside the root layout.
     */
    private void showOverview() {
        GridPane mainOverview = load("/client/views/MainOverview.fxml");
        rootLayout.setCenter(mainOverview);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

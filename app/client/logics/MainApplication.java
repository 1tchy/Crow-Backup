package client.logics;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private BorderPane rootLayout;
    private final Injector injector;

    public MainApplication() {
        this(Guice.createInjector(new ClientModule()));
    }

    public MainApplication(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crow Backup");

        rootLayout = load("/client/views/RootLayout.fxml");

        showOverview();

        primaryStage.setScene(new Scene(rootLayout));
        primaryStage.show();
    }

    @FXML
    public void stop() {
        System.out.println("closing app...");
        Platform.exit();
        System.exit(0);
    }

    private <T> T load(String fxmlResource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(injector::getInstance);
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

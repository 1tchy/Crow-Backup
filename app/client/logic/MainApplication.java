package client.logic;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApplication extends Application {

	private Stage primaryStage;
    private BorderPane rootLayout;
    
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Backupper");
        
        initRootLayout();

        showOverview();
        
        showScene();
	}
	
	public void showScene(){
		 Scene scene = new Scene(rootLayout);
         primaryStage.setScene(scene);
         primaryStage.show();
	}
	
	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("/client/views/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the backupper overview inside the root layout.
     */
    public void showOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("/client/views/BackupperOverview.fxml"));
            AnchorPane backupperOverview = (AnchorPane) loader.load();
            rootLayout.setCenter(backupperOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}
}

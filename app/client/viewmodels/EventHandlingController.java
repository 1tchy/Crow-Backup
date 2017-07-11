package client.viewmodels;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.backup.Backup;
import models.user.User;
import sun.rmi.runtime.Log;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventHandlingController {
	@FXML
	private AnchorPane anchorPane_Toolbar;
	@FXML
	private Label label_Logo;
	@FXML
	private SplitPane splitPane_Overview;
	@FXML
	private AnchorPane leftAnchorPane_Backups;
	@FXML
	private AnchorPane anchorPane_MyBackups;
	@FXML
	private AnchorPane anchorPane_MyFriends;
	@FXML
	private AnchorPane rightAnchorPane_Timeline;
	@FXML
	private Stage stage;
	@FXML
	private Button button_Help;
	@FXML
	private Button button_Settings;
	@FXML
	private Button button_Login;
	
	@FXML
	private TitledPane titledPane_MyBackups;
	@FXML
	private TitledPane titledPane_MyFriends;
	
	@FXML
	private ListView<Backup> listView_MyBackups;
	private ObservableList<Backup> listView_MyBackups_Data = FXCollections.observableArrayList();
	@FXML
	private ListView<User> listView_MyFriends;
	private ObservableList<User> listView_MyFriends_Data = FXCollections.observableArrayList();
	
	private static Logger logger = Logger.getLogger(EventHandlingController.class.getName());

	public EventHandlingController(){
		listView_MyBackups_Data.add(new Backup("Fotos", "C:\\Backups\\Fotos.zip", 55, "Daten-Backup"));
	}

	/**
	 * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
	 */
	@FXML
	private void initialize(){
		button_Help.setOnAction((event) -> { 
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Message Here...");
			alert.setHeaderText("Look, an Information Dialog");
			alert.setContentText("Maybe I can help you!");
			alert.showAndWait().ifPresent(rs -> {
		    if (rs == ButtonType.OK) {
		        System.out.println("Pressed OK.");
		    }});
		});
		
		button_Login.setOnAction((event) -> {
            Dialog<Pair<String,String>> loginDialog = new Dialog<>();
            loginDialog.setTitle("Login");
            loginDialog.setHeaderText("Look, you can login!");

            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();

            TextField username = new TextField("Username");
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");
            grid.add(new Label("Username:"),0,0);
            grid.add(username,1,0);
            grid.add(new Label("Password:"), 0,1);
            grid.add(password,1,1);
            Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            username.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            loginDialog.getDialogPane().setContent(grid);

            Platform.runLater(username::requestFocus);

            loginDialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(username.getText(), password.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = loginDialog.showAndWait();

            result.ifPresent(login -> logger.log(Level.WARNING, "do login with " + login.getKey() + "/" + login.getValue()));
		});
			
		button_Settings.setOnAction((event) -> { 
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Message Here...");
			alert.setHeaderText("Look, an Information Dialog");
			alert.setContentText("Someday you will be able to change your settings here!");
			alert.showAndWait().ifPresent(rs -> {
		    if (rs == ButtonType.OK) {
		        System.out.println("Pressed OK.");
		    }});
		});
		
		listView_MyBackups.setItems(listView_MyBackups_Data);
		listView_MyBackups.setCellFactory((list) -> {
		    return new ListCell<Backup>() {
		        @Override
		        protected void updateItem(Backup item, boolean empty) {
		            super.updateItem(item, empty);

		            if (item == null || empty) {
		                setText(null);
		            } else {
		                setText(item.getName());
		            }
		        }
		    };
		});
		
		listView_MyBackups.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
		    System.out.println("ListView Selection Changed (selected: " + newValue.toString() + ")");
		});
		//titledPane_MyBackups.expandedProperty().addListener((obs, oldValue, newValue) -> stage.sizeToScene());
		//titledPane_MyBackups.heightProperty().addListener((obs, oldHeight, newHeight) -> stage.sizeToScene());
		//titledPane_MyFriends.heightProperty().addListener((obs, oldHeight, newHeight) -> stage.sizeToScene());
	}
}

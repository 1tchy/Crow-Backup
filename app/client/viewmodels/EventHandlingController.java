package client.viewmodels;

import client.logics.connectors.server.implementations.UserServerConnector;
import client.views.LoginDialog;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.backup.Backup;
import models.user.User;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
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

    @Inject
    private UserServerConnector userServerConnector;
    @Inject
    private LoginDialog loginDialog;

    public EventHandlingController() {
        listView_MyBackups_Data.add(new Backup("Fotos", "C:\\Backups\\Fotos.zip", 55, "Daten-Backup"));
    }

    /**
     * Initializes the controller class. This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        button_Help.setOnAction((event) -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("Maybe I can help you!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        });

        button_Login.setOnAction((event) -> {
            Optional<User> result = loginDialog.showAndWait();

            result.ifPresent(login -> {
                logger.log(Level.WARNING, "do login with " + login.getMail() + "/" + login.getPasswordHash());
                //Quick hack to continue development (please feel free to clean up ;-)) //TODO!
                try {
                    logger.info("Login success: " + userServerConnector.loginAndRemember(login.getMail(), login.getPasswordHash().toCharArray()).toCompletableFuture().get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                // @todo roman: do not store password as string but only as char-array due to security reasons (it would stay in the string pool for quite some time)
                // @laurin: char-array is also in memory + transfer is as a string. Further more does the "string-caching" depend on os and java implementation.
            });
        });

        button_Settings.setOnAction((event) -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Message Here...");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("Someday you will be able to change your settings here!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
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

package client.viewmodels;

import client.logics.connectors.server.implementations.FriendshipServerConnector;
import client.logics.connectors.server.implementations.UserServerConnector;
import client.logics.queue.Command;
import client.logics.queue.CommandType;
import client.logics.queue.QueueAccessor;
import client.views.LoginDialog;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.backup.Backup;
import models.user.FriendLink;
import models.user.Friendship;
import models.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private Button button_AddFriend;

    @FXML
    private TitledPane titledPane_MyBackups;
    @FXML
    private TitledPane titledPane_MyFriends;

    @FXML
    private ListView<Backup> listView_MyBackups;
    private ObservableList<Backup> listView_MyBackups_Data = FXCollections.observableArrayList();
    @FXML
    private ListView<Friendship> listView_MyFriends;
    private ObservableList<Friendship> listView_MyFriends_Data = FXCollections.observableArrayList();

    private static Logger logger = Logger.getLogger(EventHandlingController.class.getName());

    private final UserServerConnector userServerConnector;
    private final FriendshipServerConnector friendshipServerConnector;
    private final LoginDialog loginDialog;
    private final AddFriendDialogController addFriendDialogController;
    private final QueueAccessor queueAccessor;

    private final ObservableWrapper<User> currentUser = new ObservableWrapper<>();

    @Inject
    public EventHandlingController(UserServerConnector userServerConnector, FriendshipServerConnector friendshipServerConnector, LoginDialog loginDialog, AddFriendDialogController addFriendDialogController, QueueAccessor queueAccessor) {
        this.userServerConnector = userServerConnector;
        this.friendshipServerConnector = friendshipServerConnector;
        this.loginDialog = loginDialog;
        this.addFriendDialogController = addFriendDialogController;
        this.queueAccessor = queueAccessor;
        this.listView_MyBackups_Data.add(new Backup("Fotos", "C:\\Backups\\Fotos.zip", 55, "Daten-Backup"));
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
            Optional<Login> result = loginDialog.showAndWait();

            result.ifPresent(login -> {
                logger.log(Level.WARNING, "do login with " + login.getUser());
                try {
                    logger.info("Login success: " + userServerConnector.loginAndRemember(login.getUser(), login.getPassword()).toCompletableFuture().get());
                    refreshCurrentUser();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        });

        button_AddFriend.setOnAction((event -> addFriendDialogController.show()));

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
        listView_MyBackups.setCellFactory((list) -> new ListCell<Backup>() {
            @Override
            protected void updateItem(Backup item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        listView_MyBackups.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
            System.out.println("ListView Selection Changed (selected: " + newValue.toString() + ")"));
        //titledPane_MyBackups.expandedProperty().addListener((obs, oldValue, newValue) -> stage.sizeToScene());
        //titledPane_MyBackups.heightProperty().addListener((obs, oldHeight, newHeight) -> stage.sizeToScene());
        //titledPane_MyFriends.heightProperty().addListener((obs, oldHeight, newHeight) -> stage.sizeToScene());

        listView_MyFriends.setItems(listView_MyFriends_Data);
        listView_MyFriends.setCellFactory((list) -> new ListCell<Friendship>() {
            @Override
            protected void updateItem(Friendship friendship, boolean empty) {
                super.updateItem(friendship, empty);
                if (friendship == null || empty) {
                    setText(null);
                } else {
                    FriendLink friendLink = friendship.getLinks().stream().filter(link -> link.getTo().getId() == currentUser.get().getId()).findAny().orElseThrow(() -> new RuntimeException(currentUser.get() + " has a FriendLink which maybe is not his"));
                    setText(friendLink.getFrom().getMail());
                }

            }
        });

        refreshCurrentUser();

        currentUser.addObserver((o, arg) -> friendshipServerConnector.list().thenAcceptBoth(friendshipServerConnector.openRequests(), (currentFriendships, openRequests) -> {
            List<Friendship> friendships = new ArrayList<>();
            friendships.addAll(Arrays.asList(currentFriendships));
            friendships.addAll(Arrays.asList(openRequests));
            Platform.runLater(() -> listView_MyFriends_Data.setAll(friendships));
        }), true);
    }

    public void refreshCurrentUser() {
        queueAccessor.queue(new Command(CommandType.NETWORK, 1, () -> userServerConnector.getCurrentUser().thenAccept(currentUser::set)));
    }
}

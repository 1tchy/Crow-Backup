package client.viewmodels;

import client.logics.connectors.server.implementations.FriendshipServerConnector;
import client.views.AddFriendDialog;
import client.views.GuiHelper;
import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import models.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class AddFriendDialogController {

    @Inject
    private FriendshipServerConnector friendshipServerConnector;

    public void show() {
        AddFriendDialog addFriendDialog = new AddFriendDialog();
        addFriendDialog.addMailFieldListener(createListener(addFriendDialog));
        addFriendDialog.showAndWait().ifPresent(friendshipServerConnector::addFriend);
    }

    @NotNull
    private ChangeListener<String> createListener(AddFriendDialog addFriendDialog) {
        return new ChangeListener<String>() {

            private CompletableFuture<User> findFriendFuture;

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (findFriendFuture != null) {
                    findFriendFuture.cancel(true);
                }
                boolean isMail = GuiHelper.isMail(newValue);
                setCurrentUser(null, isMail, false);
                if (isMail) {
                    findFriendFuture = friendshipServerConnector.findFriend(newValue).toCompletableFuture().whenComplete((user, throwable) -> {
                        if (throwable instanceof RuntimeException) {
                            Logger.getLogger(AddFriendDialogController.this.getClass().getName()).severe(throwable.getMessage());
                        } else {
                            setCurrentUser(user, false, true);
                        }
                    });
                }
            }

            private void setCurrentUser(@Nullable User currentUser, boolean beginningCalculation, boolean endingCalculation) {
                addFriendDialog.setCurrentUser(currentUser);
                addFriendDialog.setOkButtonDisable(currentUser == null);
                addFriendDialog.setCursor(beginningCalculation ? Cursor.WAIT : null);
                addFriendDialog.setNotFoundLabelVisible(endingCalculation && currentUser == null);
            }

        };
    }

}

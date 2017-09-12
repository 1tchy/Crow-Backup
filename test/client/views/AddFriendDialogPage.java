package client.views;

import client.logics.AbstractFXTest;
import javafx.scene.Node;

import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddFriendDialogPage {
    private static final String SEND_FRIEND_REQUEST_BUTTON = "Anfrage abschicken";
    private final AbstractFXTest driver;
    private static final String unknownMailLabel = "Kein Freund mit dieser Mailadresse gefunden.";

    public AddFriendDialogPage(AbstractFXTest driver) {
        this.driver = driver;
    }

    public void fillFriendsMail(String mail) {
        driver.write(mail);
    }

    public void sendFriendRequest() {
        driver.clickOn(SEND_FRIEND_REQUEST_BUTTON);
    }

    public void verifyFriendRequestButtonIsEnabled() {
        retryVerifyFriendRequestButton(node -> !node.isDisabled());
    }

    public void verifyFriendRequestButtonIsDisabled() {
        retryVerifyFriendRequestButton(Node::isDisabled);
    }

    private void retryVerifyFriendRequestButton(Predicate<Node> predicate) {
        long start = System.currentTimeMillis();
        boolean anfrage_abschicken = predicate.test(driver.lookup(SEND_FRIEND_REQUEST_BUTTON).query());
        while (System.currentTimeMillis() > start + 5000) {
            if (anfrage_abschicken) {
                break;
            }
            Thread.yield();
        }
        assertTrue(anfrage_abschicken);
    }

    public void verifyUnknownMailLabelVisible() {
        assertTrue(isUnknownMailLabelVisible());
    }

    private boolean isUnknownMailLabelVisible() {
        return driver.lookup(unknownMailLabel).query().isVisible();
    }

    public void verifyUnknownMailLabelHidden() {
        assertFalse(isUnknownMailLabelVisible());
    }
}

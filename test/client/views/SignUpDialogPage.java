package client.views;

import client.logics.AbstractFXTest;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("UnusedReturnValue")
public class SignUpDialogPage {
    private final AbstractFXTest driver;

    public SignUpDialogPage(AbstractFXTest driver) {
        this.driver = driver;
    }

    public SignUpDialogPage fillCreateUser(String mail, char[] password) {
        return fillCreateUser(mail, new String(password), new String(password));
    }

    public SignUpDialogPage fillCreateUser(String mail, String password1, String password2) {
        driver.write(mail).type(KeyCode.TAB).write(password1).type(KeyCode.TAB).write(password2);
        return this;
    }

    public SignUpDialogPage performCreateUser() {
        driver.clickOn("Create user");
        return this;
    }

    public void verifyCreateUserButtonIsEnabled() {
        retryVerifyCreateUserButton(node -> !node.isDisabled());
    }

    public void verifyCreateUserButtonIsDisabled() {
        retryVerifyCreateUserButton(Node::isDisabled);
    }

    private void retryVerifyCreateUserButton(Predicate<Node> predicate) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() > start + 5000) {
            if (predicate.test(driver.lookup("Create user").query())) {
                break;
            }
            Thread.yield();
        }
        assertTrue(predicate.test(driver.lookup("Create user").query()));
    }

    public static class CreateUserDialogApplicationWrapper extends FXDialogApplicationWrapper<SignUpDialog> {
        private static SignUpDialog signUpDialog;

        @Override
        SignUpDialog createDialog() {
            return signUpDialog;
        }

        public static void setSignUpDialog(SignUpDialog signUpDialog) {
            CreateUserDialogApplicationWrapper.signUpDialog = signUpDialog;
        }
    }
}

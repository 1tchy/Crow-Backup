package client.views;

import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public class GuiHelper {

    private static final Pattern MAIL_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    public static boolean hasText(String... texts) {
        return Arrays.stream(texts).allMatch(s -> s.length() > 0);
    }

    public static boolean isSame(String... texts) {
        return Arrays.stream(texts).distinct().count() <= 1;
    }

    @NotNull
    public static TextField createTextField(String id, String promptText) {
        TextField textField = new TextField();
        textField.setId(id);
        textField.setPromptText(promptText);
        return textField;
    }

    @NotNull
    public static SafePasswordField createPasswordField(String id, String promptText) {
        SafePasswordField passwordField = new SafePasswordField();
        passwordField.setId(id);
        passwordField.setPromptText(promptText);
        return passwordField;
    }

    public static boolean isMail(String mail) {
        return MAIL_REGEX.matcher(mail).matches();
    }
}

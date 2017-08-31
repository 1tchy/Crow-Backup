package client.views;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GuiHelper {
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
    public static PasswordField createPasswordField(String id, String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setId(id);
        passwordField.setPromptText(promptText);
        return passwordField;
    }

}

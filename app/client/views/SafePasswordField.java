package client.views;

import javafx.scene.control.PasswordField;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SafePasswordField extends PasswordField {
    private char[] result;

    public SafePasswordField() {
        result = new char[]{};
    }

    public void clear() {
        Arrays.fill(result, (char) 0);
    }

    private char[] getPasswordThrowing() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Content content = getContent();

        Field field = content.getClass().getDeclaredField("characters");
        field.setAccessible(true);

        StringBuilder stringBuilder = (StringBuilder) field.get(content);

        clear();
        result = new char[stringBuilder.length()];
        stringBuilder.getChars(0, stringBuilder.length(), result, 0);

        return result;
    }

    public final char[] getPassword() {
        try {
            return getPasswordThrowing();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new char[]{};
    }

}

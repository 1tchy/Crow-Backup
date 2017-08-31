package client.views;

import javafx.scene.control.PasswordField;

import java.lang.reflect.Field;

public class SafePasswordField extends PasswordField {

    private char[] getPasswordThrowing() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Content content = getContent();

        Field field = content.getClass().getDeclaredField("characters");
        field.setAccessible(true);

        StringBuilder stringBuilder = (StringBuilder) field.get(content);
        char[] result = new char[stringBuilder.length()];
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

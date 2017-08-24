package client.views;

import java.util.Arrays;

public class InputCheckerHelper {
    public static boolean hasText(String... texts) {
        return Arrays.stream(texts).allMatch(s -> s.length() > 0);
    }

    public static boolean isSame(String... texts) {
        return !Arrays.stream(texts).allMatch(s -> s.length() > 0)
            && Arrays.stream(texts).distinct().count() == 1;
    }
}

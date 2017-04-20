package helpers;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneralHelpers {

    public static <T> void assertOptionalEquals(T expected, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<T> actual) {
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

}

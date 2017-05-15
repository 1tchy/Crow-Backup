package helpers;

import models.BaseEntity;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneralHelpers {

    public static <T> void assertOptionalEquals(T expected, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<T> actual) {
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    public static <T extends BaseEntity> T setIdForTest(T entity, int id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
        return entity;
    }
}

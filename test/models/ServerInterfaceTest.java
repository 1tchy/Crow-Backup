package models;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ServerInterfaceTest {

    @Test
    public void test_thatAllMethodsReturnPromise() {
        //Arrange
        Set<Class<?>> interfaces = new Reflections("models.interfaces", new SubTypesScanner(false)).getSubTypesOf(Object.class);
        assertThat(interfaces, not(empty()));
        for (Class<?> anInterface : interfaces) {
            for (Method method : anInterface.getDeclaredMethods()) {
                //Act
                Class<?> methodReturnType = method.getReturnType();
                //Assert
                assertEquals(method + " soll ein CompletionStage zurückgeben", CompletionStage.class, methodReturnType);
            }
        }
    }

}
package models;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;

public class ServerInterfaceTest {

    @Test
    public void test_thatAllMethodsReturnPromise() {
        //Arrange
        for (Method method : ServerInterface.class.getDeclaredMethods()) {
            //Act
            Class<?> methodReturnType = method.getReturnType();
            //Assert
            assertEquals(method + " soll ein CompletionStage zurückgeben", CompletionStage.class, methodReturnType);
        }
    }

}
package client.logic.connectors.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestServerConnectorTest extends AbstractServerConnectorTest {

    @InjectMocks
    private TestServerConnector cut;

    @Test
    public void test_getCallerName() {
        //Arrange
        //Act
        final Method actual = delegateCallingMethod();
        //Assert
        assertEquals("test_getCallerName", actual.getName());
        assertEquals(this.getClass(), actual.getDeclaringClass());

    }

    private Method delegateCallingMethod() {
        return cut.getCallingMethod();
    }

    @Test
    public void test_simpleMethodCall() throws ExecutionException, InterruptedException {
        //Arrange
        jsonResponseContent = "\"Hallo Welt\"";
        //Act
        CompletionStage<String> actualPromise = cut.helloWorld("Welt");
        //Assert
        String actual = actualPromise.toCompletableFuture().get();
        assertEquals("Hallo Welt", actual);
    }

}
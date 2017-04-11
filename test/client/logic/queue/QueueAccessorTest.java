package client.logic.queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueueAccessorTest {

    private boolean test1_success = false;

    private QueueAccessor cut;

    @Before
    public void setUp() {
        cut = new QueueAccessor();
    }

    @After
    public void tearDown() throws InterruptedException {
        cut.terminate(10, TimeUnit.SECONDS);
    }

    @Test(timeout = 1000)
    public void run_test1() {
        //Arrange
        CommandInterface myCommand = new Command(CommandType.CPU, 5, () -> test1_success = true);
        //Act
        cut.queue(myCommand);
        //Assert
        while (true) {
            if (test1_success) {
                break;
            }
            Thread.yield();
        }
        assertTrue(test1_success);
    }

    @Test
    public void test_that_allCommandTypesHaveAnExecutor() {
        //Arrange
        //Act
        for (CommandType commandType : CommandType.values()) {
            cut.queue(new Command(commandType, 5, () -> {
            }));
        }
        //Assert (keine Exception)
    }

    @Test(expected = RejectedExecutionException.class)
    public void test_that_shutdownWorks() throws InterruptedException {
        //Arrange
        //Act
        cut.terminate(3, TimeUnit.SECONDS);
        //Assert
        cut.queue(new Command(CommandType.CPU, 5, () -> {
        }));
    }

    @Test
    public void test_that_costsStartAt0() {
        //Arrange
        //Act
        //Assert
        assertEquals(0, cut.getQueuedCosts(CommandType.CPU, false));
    }

    @Test
    public void test_that_costsAreCalculated() {
        //Arrange
        //Act
        cut.queue(new Command(CommandType.CPU, 5, () -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }));
        //Assert
        assertEquals(5, cut.getQueuedCosts(CommandType.CPU, true));
    }

}
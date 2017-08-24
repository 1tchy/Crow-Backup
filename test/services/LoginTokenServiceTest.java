package services;

import com.typesafe.config.ConfigFactory;
import helpers.GeneralHelpers;
import helpers.WithTransaction;
import models.user.User;
import org.junit.Before;
import org.junit.Test;
import play.Environment;
import play.Mode;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class LoginTokenServiceTest extends WithTransaction {

    private LoginTokenService cut;

    @Before
    public void setup() {
        cut = new LoginTokenService(persistenceService, ConfigFactory.parseMap(Collections.singletonMap(LoginTokenService.CRYPTO_SECRET, "dEMr6BEqeDKdG2n")), new Environment(Mode.TEST));
    }

    @Test
    public void test_simple_doSignToken() throws Exception {
        //Arrange
        User user = GeneralHelpers.setIdForTest(new User(), 42);
        //Act
        String actual = cut.create(user);
        //Assert
        assertThat(actual, containsString(String.valueOf(user.getId())));
        assertEquals("Token 42-gY3HTA44X0yynRh9eLrGEK2zUAw=", actual);
    }

    @Test
    public void test_that_doSignToken_alwaysReturnsTheSameForTheSameInput() throws Exception {
        //Arrange
        User user = new User();
        //Act
        String actual1 = cut.create(user);
        String actual2 = cut.create(user);
        //Assert
        assertEquals(actual1, actual2);
    }

    @Test
    public void test_that_doSignToken_alwaysReturnsSomethingDifferentForDifferentInput() throws Exception {
        //Arrange
        User user1 = GeneralHelpers.setIdForTest(new User(), 1);
        User user2 = GeneralHelpers.setIdForTest(new User(), 2);
        //Act
        String actual1 = cut.create(user1);
        String actual2 = cut.create(user2);
        //Assert
        assertNotEquals(actual1, actual2);
    }

}

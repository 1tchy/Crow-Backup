package services;

import helpers.WithTransaction;
import models.user.User;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PersistenceServiceTest extends WithTransaction {

    private PersistenceService cut;

    @Before
    public void setup() {
        cut = new PersistenceService(jpaApi);
    }

    @Test
    public void test_that_persistGeneratesIds() throws Exception {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        assertEquals(0, user.getId());
        //Act
        cut.persist(user);
        //Assert
        assertNotEquals(0, user.getId());
    }

    @Test
    public void test_that_persistedItemsCanBeFoundAgain() {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        cut.persist(user);
        long id = user.getId();
        //Act
        User actual = cut.readUnique(User.class, id);
        //Assert
        assertEquals("test@test.com", actual.getMail());
    }

    @Test
    public void test_that_elementsCanBeFoundByUniqueValue() {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        cut.persist(user);
        //Act
        User actual = cut.readUnique(User.class, "mail", "test@test.com");
        //Assert
        assertEquals("test@test.com", actual.getMail());
    }

    @Test(expected = PersistenceException.class)
    public void test_detach() {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        cut.persist(user);
        //Act
        cut.detach(user);
        //Assert
        cut.persist(user);
    }

}
package services;

import helpers.WithTransaction;
import models.user.User;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PersistenceServiceTest extends WithTransaction {

    @Test
    public void test_that_persistGeneratesIds() throws Exception {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        assertEquals(0, user.getId());
        //Act
        persistenceService.persist(user);
        //Assert
        assertNotEquals(0, user.getId());
    }

    @Test
    public void test_that_persistedItemsCanBeFoundAgain() {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        persistenceService.persist(user);
        long id = user.getId();
        //Act
        User actual = persistenceService.readUnique(User.class, id);
        //Assert
        assertEquals("test@test.com", actual.getMail());
    }

    @Test
    public void test_that_elementsCanBeFoundByUniqueValue() {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        persistenceService.persist(user);
        //Act
        User actual = persistenceService.readUnique(User.class, "mail", "test@test.com");
        //Assert
        assertEquals("test@test.com", actual.getMail());
    }

    @Test(expected = PersistenceException.class)
    public void test_detach() {
        //Arrange
        User user = new User();
        user.setMail("test@test.com");
        persistenceService.persist(user);
        //Act
        persistenceService.detach(user);
        //Assert
        persistenceService.persist(user);
    }

}
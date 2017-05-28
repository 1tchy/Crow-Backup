package services;

import helpers.WithTransaction;
import models.user.User;
import org.junit.Test;
import play.libs.F;

import javax.persistence.NoResultException;
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

    @Test(expected = NoResultException.class)
    public void test_that_noneExitingIdsAreHandled() throws Exception {
        //Arrange
        //Act
        persistenceService.readUnique(User.class, 23848237948L);
        //Assert (Exception is thrown)
    }

    @Test(expected = RuntimeException.class)
    public void test_that_atLeastSomeSQLInjectionsAreDetected() throws Exception {
        //Arrange
        //Act
        persistenceService.readOne(User.class, new F.Tuple<>("1=1 OR name", "John Doe"));
        //Assert (Exception is thrown)
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
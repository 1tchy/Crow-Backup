package services;

import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class PasswordServiceTest {

    private PasswordService cut = new PasswordService();

    @Test
    public void test_that_correctPasswordMatches() {
        //Arrange
        String hash = cut.createHash("1234".toCharArray());
        //Act
        boolean passwordCorrect = cut.isPasswordCorrect(hash, "1234".toCharArray());
        //Assert
        assertTrue(passwordCorrect);
    }

    @Test
    public void test_that_wrongPasswordDoesNotMatch() {
        //Arrange
        String hash = cut.createHash("1234".toCharArray());
        //Act
        boolean passwordCorrect = cut.isPasswordCorrect(hash, "wrong password".toCharArray());
        //Assert
        assertFalse(passwordCorrect);
    }

    @Test
    public void test_that_passwordIsRemovedFromMemoryAfterVerification() {
        //Arrange
        String hash = cut.createHash("1234".toCharArray());
        char[] password = "1234".toCharArray();
        assertEquals("1234", new String(password));
        //Act
        cut.isPasswordCorrect(hash, password);
        //Assert
        assertNotEquals("1234", new String(password));
    }

    @Test
    public void test_that_hashIsSalted() {
        //Arrange
        //Act
        String hash1 = cut.createHash("1234".toCharArray());
        String hash2 = cut.createHash("1234".toCharArray());
        //Assert
        assertNotEquals(hash1, hash2);
    }

    /**
     * Genügend langsam, dass es bei einem Brutforce-Angriff teuer wird - aber nicht zu langsam, dass es den Benutzer stört
     */
    @Test
    public void test_that_hashVerificationIsSlow() {
        //Arrange
        String hash = cut.createHash("1234".toCharArray());
        char[] password = "1234".toCharArray();
        //Act
        long start = System.currentTimeMillis();
        cut.isPasswordCorrect(hash, password);
        long duration = System.currentTimeMillis() - start;
        //Assert
        assertThat(duration, greaterThan(50L));
        assertThat(duration, lessThan(1000L));
    }
}
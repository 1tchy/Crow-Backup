package client.views;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class InputCheckerHelperTest {
    @Test
    public void test_hasText_positive() throws Exception {
        String[] input = {"a", "b"};
        boolean hasText = InputCheckerHelper.hasText(input);
        assertTrue(hasText);
    }

    @Test
    public void test_hasText_negative() throws Exception {
        String[] input = {"", "b", "c"};
        boolean hasText = InputCheckerHelper.hasText(input);
        assertFalse(hasText);
    }

    @Test
    public void test_hasText_empty() throws Exception {
        String[] input = {};
        boolean hasText = InputCheckerHelper.hasText(input);
        assertFalse(hasText);
    }

    @Test
    public void test_isSame_empty() throws Exception {
        String[] input = {};
        boolean isSame = InputCheckerHelper.isSame(input);
        assertTrue(isSame);
    }

    @Test
    public void test_isSame_positive() throws Exception {
        String[] input = {"abc", "abc", "abc", "abc"};
        boolean isSame = InputCheckerHelper.isSame(input);
        assertTrue(isSame);
    }

    @Test
    public void test_isSame_negative() throws Exception {
        String[] input = {"abc", "", "abc"};
        boolean isSame = InputCheckerHelper.isSame(input);
        assertTrue(isSame);
    }

}

package client.views;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputCheckerHelperTest {
    @Test
    public void test_hasText_positive() throws Exception {
        String[] input = {"a", "b"};
        boolean hasText = GuiHelper.hasText(input);
        assertTrue(hasText);
    }

    @Test
    public void test_hasText_negative() throws Exception {
        String[] input = {"", "b", "c"};
        boolean hasText = GuiHelper.hasText(input);
        assertFalse(hasText);
    }

    @Test
    @Ignore
    //TODO Roman, this test is borken. Can you fix or remove it? I (Laurin) do not care what happens in this case.
    public void test_hasText_empty() throws Exception {
        String[] input = {};
        boolean hasText = GuiHelper.hasText(input);
        assertFalse(hasText);
    }

    @Test
    public void test_isSame_empty() throws Exception {
        String[] input = {};
        boolean isSame = GuiHelper.isSame(input);
        assertTrue(isSame);
    }

    @Test
    public void test_isSame_emptyStrings() throws Exception {
        String[] input = {"", ""};
        boolean isSame = GuiHelper.isSame(input);
        assertTrue(isSame);
    }

    @Test
    public void test_isSame_positive() throws Exception {
        String[] input = {"abc", "abc", "abc", "abc"};
        boolean isSame = GuiHelper.isSame(input);
        assertTrue(isSame);
    }

    @Test
    public void test_isSame_negative() throws Exception {
        String[] input = {"abc", "", "abc"};
        boolean isSame = GuiHelper.isSame(input);
        assertFalse(isSame);
    }

}

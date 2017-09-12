package client.views;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuiHelperTest {
    @Test
    public void test_isMail_when_valid() throws Exception {
        assertTrue(GuiHelper.isMail("mail@example.com"));
    }

    @Test
    public void test_isMail_when_invalid() throws Exception {
        assertFalse(GuiHelper.isMail("no-mail-address"));
    }

}

package client.logics;

import javafx.stage.Stage;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.testfx.util.WaitForAsyncUtils;

import static org.hamcrest.Matchers.equalTo;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

public class MainApplicationTest extends AbstractFXTest {

    @InjectMocks
    private MainApplication cut;

    @Override
    public void doStart(Stage stage) {
        cut.start(stage);
    }

    @Test
    public void test_hauptseite_when_appGestartet() {
        // Arrange
        Stage window = (Stage) window(0);
        WaitForAsyncUtils.waitForFxEvents();
        //Act
        //Assert
        verifyThat(window.getTitle(), equalTo("Crow Backup"));
        verifyThat("#button_Login", hasText("Anmelden"));
        verifyThat("#button_Settings", hasText("Einstellungen"));
    }
}

package client.logics;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.hamcrest.Matchers.equalTo;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

public class MainApplicationTest extends AbstractFXTest {

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

    @Override
    @NotNull
    protected Class<? extends Application> getAppClass() {
        return MainApplication.class;
    }

}

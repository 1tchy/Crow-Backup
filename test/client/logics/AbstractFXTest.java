package client.logics;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractFXTest extends ApplicationTest {

	private static final boolean HEADLESS = true;
	private static final Map<String, String> HEADLESS_PROPERTIES = Collections.unmodifiableMap(Stream.of(
			new AbstractMap.SimpleEntry<>("testfx.robot", "glass"),
			new AbstractMap.SimpleEntry<>("testfx.headless", "true"),
			new AbstractMap.SimpleEntry<>("prism.order", "sw"),
			new AbstractMap.SimpleEntry<>("prism.text", "t2k"),
			new AbstractMap.SimpleEntry<>("java.awt.headless", "true"))
			.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

	@BeforeClass
	public static void initHeadlessMode() {
		setHeadlessMode(HEADLESS);
	}

	@SuppressWarnings("WeakerAccess") //for easier switch during development protected
	protected static void setHeadlessMode(boolean activate) {
		for (Map.Entry<String, String> property : HEADLESS_PROPERTIES.entrySet()) {
			if (activate) {
				System.setProperty(property.getKey(), property.getValue());
			} else {
				System.clearProperty(property.getKey());
			}
		}
	}

	@Before
	public void setup() throws Exception {
		launch(getAppClass());
	}

	@NotNull
	protected abstract Class<MainApplication> getAppClass();

	@Override
	public void start(Stage stage) {
		stage.show();
	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.hideStage();
		release(new KeyCode[]{});
		release(new MouseButton[]{});
	}

}

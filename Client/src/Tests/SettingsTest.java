package Tests;

import Utils.Settings;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SettingsTest {

    private static Logger testLogger;

    private Settings settings;
    private final String settingsPath = "out/testEnv/SPSTestSettings";

    @BeforeClass
    public static void beforeClass(){
        testLogger = setUpLogger();
    }

    @Before
    public void setUp() throws Exception {
        settings = new Settings(settingsPath, testLogger);
    }

    @After
    public void tearDown() throws Exception {
        if (Files.exists(Paths.get(settingsPath))) {
            File file = new File(settingsPath);
            if(!file.delete()){
                throw new Exception("Unable to delete " + settingsPath);
            }
        }
    }

    @Test
    public void firstRun() throws Exception {
        settings.readSettings();

        Assert.assertTrue(Files.exists(Paths.get(settingsPath)));
        Assert.assertEquals(8845, settings.getPort());
        Assert.assertNotNull(settings.getIp());
    }

    @Test
    public void saveSettings() throws Exception {
        settings.updateSettings(6666, "127.0.0.1");
        settings.saveSettings();

        Vector<String> lines = new Vector<>();
        Files.lines(Paths.get(settingsPath)).forEachOrdered(str -> lines.add(str));

        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("6666", lines.get(0));
        Assert.assertEquals("127.0.0.1", lines.get(1));
    }

    @Test
    public void updateSettings() throws Exception {
        settings.updateSettings(6666, "127.0.0.1");

        Assert.assertEquals(6666, settings.getPort());
        Assert.assertEquals("127.0.0.1", settings.getIp());
    }

    private static Logger setUpLogger() {
        Logger logger = Logger.getLogger("SPSClientTestLogger");
        logger.setUseParentHandlers(false);
        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler("out/testEnv/TestLogs.log", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return logger;
    }

}
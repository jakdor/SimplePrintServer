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
    private final String SETTINGS_PATH = "out/testEnv/SPSTestSettings";

    private final String IP = "127.0.0.1";
    private final String LAST_PATH = "dir/testStr";
    private final String LAST_DIR = "dir";
    private final int PORT = 6666;
    private final boolean LOGGING = true;
    private final int CONFIG_MODE = 1;

    @BeforeClass
    public static void beforeClass(){
        testLogger = setUpLogger();
    }

    @Before
    public void setUp() throws Exception {
        settings = new Settings(SETTINGS_PATH, testLogger);
    }

    @After
    public void tearDown() throws Exception {
        if (Files.exists(Paths.get(SETTINGS_PATH))) {
            File file = new File(SETTINGS_PATH);
            if(!file.delete()){
                throw new Exception("Unable to delete " + SETTINGS_PATH);
            }
        }
    }

    @Test
    public void firstRun() throws Exception {
        settings.readSettings();

        Assert.assertTrue(Files.exists(Paths.get(SETTINGS_PATH)));
        Assert.assertEquals(8845, settings.getPort());
        Assert.assertNotNull(settings.getIp());
        Assert.assertEquals("", settings.getLastPath());
        Assert.assertEquals("", settings.getLastPathDir());
        Assert.assertEquals(false, settings.isLogging());
        Assert.assertEquals(0, settings.getConfigMode());
    }

    @Test
    public void saveSettings() throws Exception {
        settings.updateSettings(PORT, IP, LAST_PATH, LAST_DIR, LOGGING, CONFIG_MODE);
        settings.saveSettings();

        Vector<String> lines = new Vector<>();
        Files.lines(Paths.get(SETTINGS_PATH)).forEachOrdered(lines::add);

        Assert.assertEquals(6, lines.size());
        Assert.assertEquals(Integer.toString(PORT), lines.get(0));
        Assert.assertEquals(IP, lines.get(1));
        Assert.assertEquals(LAST_PATH, lines.get(2));
        Assert.assertEquals(LAST_DIR, lines.get(3));
        Assert.assertEquals(LOGGING, Boolean.parseBoolean(lines.get(4)));
        Assert.assertEquals(CONFIG_MODE, Integer.parseInt(lines.get(5)));
    }

    @Test
    public void updateSettings() throws Exception {
        settings.updateSettings(PORT, IP, LAST_PATH, LAST_DIR, LOGGING, CONFIG_MODE);

        Assert.assertEquals(PORT, settings.getPort());
        Assert.assertEquals(IP, settings.getIp());
        Assert.assertEquals(LAST_PATH, settings.getLastPath());
        Assert.assertEquals(LAST_DIR, settings.getLastPathDir());
        Assert.assertEquals(LOGGING, settings.isLogging());
        Assert.assertEquals(CONFIG_MODE, settings.getConfigMode());
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
package Tests;

import Utils.Settings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class SettingsTest {

    private Settings settings;
    private final String settingsPath = "out/testEnv/SPSTestSettings";

    @Before
    public void setUp() throws Exception {
        settings = new Settings(settingsPath);
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
        Assert.assertNotNull(settings.getSavePath());
        Assert.assertEquals(true, settings.isLogging());
        Assert.assertEquals(true, settings.isOpenOptions());
    }

    @Test
    public void saveSettings() throws Exception {
        settings.updateSettings(6666, "/totally/invalid/path", false, true);
        settings.saveSettings();

        Vector<String> lines = new Vector<>();
        Files.lines(Paths.get(settingsPath)).forEachOrdered(lines::add);

        Assert.assertEquals(4, lines.size());
        Assert.assertEquals("6666", lines.get(0));
        Assert.assertEquals("/totally/invalid/path", lines.get(1));
        Assert.assertEquals("false", lines.get(2));
        Assert.assertEquals("true", lines.get(3));
    }

    @Test
    public void updateSettings() throws Exception {
        settings.updateSettings(6666, "/totally/invalid/path", false, true);

        Assert.assertEquals(6666, settings.getPort());
        Assert.assertEquals("/totally/invalid/path", settings.getSavePath());
        Assert.assertEquals(false, settings.isOpenOptions());
        Assert.assertEquals(true, settings.isLogging());
    }

}
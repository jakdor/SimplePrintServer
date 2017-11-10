package Tests;

import Commands.Command;
import Commands.CommandsManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.Assert.*;

public class CommandsManagerTest {

    private CommandsManager commandsManager;
    private Logger logger;

    private static String testPath = "out/testEnv";
    private static String deletePath = "out/testEnv/.SPSCommands";

    private Command command1 = new Command("dupa1", "test1", "test1v2");

    @Before
    public void setUp() throws Exception {
        logger = setUpLogger();

        commandsManager = new CommandsManager(testPath, logger);
        commandsManager.add(command1);
        commandsManager.add(command1);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (Files.exists(Paths.get(deletePath))) {
            File file = new File(deletePath);
            if(!file.delete()){
                throw new Exception("Unable to delete " + deletePath);
            }
        }
    }

    @Test
    public void saveTest() throws Exception {
        commandsManager.writeCommands();
        CommandsManager testCommandManager = new CommandsManager(testPath, logger);
        testCommandManager.readCommands();

        Assert.assertEquals(2, testCommandManager.size());
        Assert.assertEquals("dupa1", testCommandManager.get(0).getName());
        Assert.assertTrue(testCommandManager.get(1).equals(command1));
    }

    @Test
    public void getListTest() throws Exception {
        List<Command> testVector = commandsManager.getCommandList();

        Assert.assertTrue(testVector instanceof Vector);
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
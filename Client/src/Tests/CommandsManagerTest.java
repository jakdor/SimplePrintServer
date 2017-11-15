package Tests;

import Commands.Command;
import Commands.CommandsManager;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CommandsManagerTest {

    private CommandsManager commandsManager;
    private Logger logger;

    private final String TEST_PATH = "out/testEnv";
    private final String DELETE_PATH = "out/testEnv/.SPSCommands";

    private Command command1 = new Command("dupa1", "test1", "test1v2", ".pdf");
    private Command command2 = new Command("dupa2", "test2", "test2v2", ".txt");

    private List<Command> testList;

    @Before
    public void setUp() throws Exception {
        logger = setUpLogger();
        testList = new Vector<>();

        testList.add(command1);
        testList.add(command1);
        testList.add(command2);

        commandsManager = new CommandsManager(TEST_PATH, logger);
        commandsManager.add(testList.get(0));
        commandsManager.add(testList.get(1));
        commandsManager.add(testList.get(2));
    }

    @After
    public void tearDown() throws Exception {
        if (Files.exists(Paths.get(DELETE_PATH))) {
            File file = new File(DELETE_PATH);
            if(!file.delete()){
                throw new Exception("Unable to delete " + DELETE_PATH);
            }
        }
    }

    @Test
    public void saveTest() throws Exception {
        commandsManager.writeCommands();
        CommandsManager testCommandManager = new CommandsManager(TEST_PATH, logger);
        testCommandManager.readCommands();

        Assert.assertEquals(testList.size(), testCommandManager.size());

        Assert.assertEquals("dupa1", testCommandManager.get(0).getName());
        Assert.assertEquals(".pdf", testCommandManager.get(0).getFileFormat());
        Assert.assertEquals("test1", testCommandManager.get(0).getPrintCommand());
        Assert.assertEquals("test1v2", testCommandManager.get(0).getOpenCommand());

        for(int i = 0; i < testList.size(); ++i){
            Assert.assertTrue(testList.get(i).equals(testCommandManager.get(i)));
        }
    }

    @Test
    public void getListTest() throws Exception {
        List<Command> testVector = commandsManager.getCommandList();

        Assert.assertTrue(testVector instanceof Vector);
    }

    @Test
    public void getFirstFileFormatIndexTest() throws Exception {
        Assert.assertEquals(0, commandsManager.getFirstFileFormatIndex(".pdf"));
        Assert.assertEquals(2, commandsManager.getFirstFileFormatIndex(".txt"));
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
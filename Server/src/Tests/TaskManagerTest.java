package Tests;

import Network.TaskManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskManagerTest {

    private TaskManager taskManager;
    private final String testStr = "ls#|#1#!#FileName.pdf#@#File here"; //<command>#|#<mode>#!#<fileName>#@#<fileBytes>
    private final String testStr2 = "mkdir out/testEnv/testDir123#|#1#!#test.txt#@#Hello world!";
    private final String testStr3 = "ls#|#0#!#test.txt#@#It's over Anakin, I have the high ground!";
    private final String testSavePath = "out/testEnv";

    @Before
    public void setUp() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr);
        taskManager.parse();
    }

    @After
    public void tearDown() throws Exception {
        String[] toBeDeleted = {"FileName.pdf", "test.txt", "testDir123"};

        for(String str : toBeDeleted){
            if (Files.exists(Paths.get(testSavePath + "/" + str))) {
                File file = new File(testSavePath + "/" + str);
                if(!file.delete()){
                    throw new Exception("Unable to delete " + str);
                }
            }
        }
    }

    @Test
    public void parseTest() throws Exception {
        Assert.assertEquals("ls", taskManager.getCommand());
        Assert.assertEquals("FileName.pdf", taskManager.getFileName());
        Assert.assertEquals("File here", taskManager.getFileStr());
        Assert.assertEquals(1, taskManager.getMode());
    }

    @Test
    public void saveTest() throws Exception {
        taskManager.saveFile();
        byte[] file = Files.readAllBytes(Paths.get(testSavePath + "/" + taskManager.getFileName()));
        Assert.assertEquals("File here", new String(file));
    }

    @Test
    public void saveTempTest() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr3);
        taskManager.parse();
        taskManager.saveFile();

        System.out.println("temp file: " + taskManager.getTempFilePath());

        byte[] file = Files.readAllBytes(Paths.get(taskManager.getTempFilePath()));
        Assert.assertEquals("It's over Anakin, I have the high ground!", new String(file));
    }

    @Test
    public void IntegrationTest1() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr2);
        taskManager.parse();
        taskManager.saveFile();
        taskManager.execute();

        byte[] file = Files.readAllBytes(Paths.get(testSavePath + "/" + taskManager.getFileName()));

        Assert.assertEquals("Hello world!", new String(file));
        Assert.assertTrue(Files.exists(Paths.get(testSavePath + "/" + "testDir123")));
    }
}
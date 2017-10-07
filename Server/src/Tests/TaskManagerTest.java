package Tests;

import Network.TaskManager;
import org.junit.Assert;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskManagerTest {

    private TaskManager taskManager;
    private final String testStr = "Command here#|#1#!#FileName.pdf#@#File here";
    private final String testSavePath = "out";

    @org.junit.Before
    public void setUp() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr);
        taskManager.parse();
    }

    @org.junit.Test
    public void parseTest() throws Exception {
        Assert.assertEquals(taskManager.getCommand(), "Command here");
        Assert.assertEquals(taskManager.getFileName(), "FileName.pdf");
        Assert.assertEquals(taskManager.getFileStr(), "File here");
        Assert.assertEquals(taskManager.getMode(), 1);
    }

    @org.junit.Test
    public void saveTest() throws Exception {
        taskManager.saveFile();
        byte[] file = Files.readAllBytes(Paths.get("out/" + taskManager.getFileName()));
        Assert.assertEquals(new String(file), "File here");
    }
}
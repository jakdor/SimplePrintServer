package Tests;

import Network.TaskManager;
import org.junit.Assert;

public class TaskManagerTest {

    private TaskManager taskManager;
    private final String testStr = "Command here#|#1#!#FileName.pdf#@#File here";
    private final String testSavePath = "";

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

}
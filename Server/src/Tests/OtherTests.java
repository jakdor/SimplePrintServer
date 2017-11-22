package Tests;

import Network.TaskManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OtherTests {

    private final String testSavePath = "out/testEnv";
    private TaskManager taskManager;

    @Before
    public void setUp() throws Exception {
        taskManager = new TaskManager(testSavePath, ""); //only for writeToFile()
    }

    private byte[] readFile(String filePath) throws IOException { //same function on client-side
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    @Test
    public void readAndSaveTest() throws Exception {
        byte[] data = readFile(testSavePath + "/test.pdf");
        taskManager.writeToFile(testSavePath + "/testOut.pdf", data);
        byte[] data2 = readFile(testSavePath + "/testOut.pdf");

        Assert.assertEquals(data.length, data2.length);
        Assert.assertEquals(new String(data), new String(data2));

        Desktop.getDesktop().open(new File(testSavePath + "/testOut.pdf")); //visual confirmation
    }
}

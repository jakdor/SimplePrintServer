package Tests;

import Network.Carrier;
import Network.TaskManager;
import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class TaskManagerTest {

    private TaskManager taskManager;
    private final String testSavePath = "out/testEnv";
    private static String testStr, testStr2, testStr3, testStr4;

    private static final Carrier carrier
            = new Carrier("ls", 1, "FileName.pdf", ("File here").getBytes());
    private static final Carrier carrier2
            = new Carrier("mkdir out/testEnv/testDir123", 0, "test.txt", ("Hello world!").getBytes());
    private static final Carrier carrier3
            = new Carrier("ls", 0, "test.txt", ("It's over Anakin, I have the high ground!").getBytes());
    private static final Carrier carrier4
            = new Carrier("mkdir %FILE%2", 0, "test", ("It's over Anakin, I have the high ground!").getBytes());

    private static String serialize( Serializable serializable ) throws IOException { //same function on client-side
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayOutputStream );
        objectOutputStream.writeObject( serializable );
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    private byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    @BeforeClass
    public static void prepareCarrierObj() throws Exception{
        testStr = serialize(carrier);
        testStr2 = serialize(carrier2);
        testStr3 = serialize(carrier3);
        testStr4 = serialize(carrier4);
    }

    @Before
    public void setUp() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr);
        taskManager.parse();
    }

    @After
    public void tearDown() throws Exception {
        String[] toBeDeleted = {"FileName.pdf", "test.txt", "testDir123", "test2"};

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
        Assert.assertEquals("File here", new String(taskManager.getFileData()));
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
    public void insertFilePathToCommandTest() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr4);
        taskManager.parse();
        taskManager.saveFile();
        taskManager.execute();

        Assert.assertTrue(Files.exists(Paths.get(testSavePath + "/" + "test2")));
    }

    @Test
    public void integrationTest1() throws Exception {
        taskManager = new TaskManager(testSavePath, testStr2);
        taskManager.parse();
        taskManager.saveFile();
        taskManager.execute();

        byte[] file = Files.readAllBytes(Paths.get(taskManager.getTempFilePath()));

        Assert.assertEquals("Hello world!", new String(file));
        Assert.assertTrue(Files.exists(Paths.get(testSavePath + "/" + "testDir123")));
    }

    @Test
    public void base64FileSerializationTest() throws Exception {
        byte[] fileData = readFile(testSavePath + "/test.pdf");

        Carrier carrier = new Carrier("mkdir %FILE%2", 0, "test", fileData);

        String serializedCarrier = serialize(carrier);
        Carrier carrier2 = (Carrier) taskManager.deserialize(serializedCarrier);

        Assert.assertTrue(carrier.equals(carrier2));
        Assert.assertEquals(new String(carrier.getFileData()), new String(carrier2.getFileData()));
    }
}
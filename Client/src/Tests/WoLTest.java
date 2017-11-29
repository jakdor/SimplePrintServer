package Tests;

import Network.WoL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.DatagramPacket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class WoLTest {

    private static Logger testLogger;
    private WoL woL;

    private final String MAC = "54:04:A6:4A:A7:CC";

    @BeforeClass
    public static void beforeClass(){
        WoLTest.testLogger = setUpLogger();
    }

    @Before
    public void setUp() throws Exception {
        this.woL = new WoL(WoLTest.testLogger);
    }

    @Test
    public void wakeOnLanTest() throws Exception {
        woL.wake(woL.formatBroadcastIP(), MAC);
    }

    @Test
    public void dataPacketTest() throws Exception {
        DatagramPacket datagramPacket = woL.getPacket(woL.formatBroadcastIP(), MAC);
        byte[] bytes = datagramPacket.getData();

        Assert.assertEquals(6 + 16*6, bytes.length);
    }

    @Test
    public void broadcastIpTest() throws Exception {
        String broadcastIp = woL.formatBroadcastIP();

        Assert.assertTrue(broadcastIp.endsWith("255"));
        Assert.assertTrue(broadcastIp.length() > 8);
        Assert.assertTrue(broadcastIp.startsWith("192")
                || broadcastIp.startsWith("172") || broadcastIp.startsWith("10"));
    }

    @Test
    public void getMacBytesTest() throws Exception {
        byte[] bytes = woL.getMacBytes(MAC);

        Assert.assertEquals(6, bytes.length);
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
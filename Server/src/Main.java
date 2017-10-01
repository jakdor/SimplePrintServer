import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main
{
    static Logger logger;

    private static NetworkManager networkManager;

    public static void main(String[] args) throws Exception
    {
        logger = setUpLogger();

        networkManager = new NetworkManager();
        networkManager.startServer(8845);

        while(true)
        {
            try{
                Thread.sleep(10);
            }
            catch (Exception e){
                System.out.println("Error: unable to skip 10ms, " + e.toString());
            }

            networkManager.readMessage();
        }
    }

    private static Logger setUpLogger(){
        Logger logger = Logger.getLogger("SPSLogger");
        logger.setUseParentHandlers(false);
        FileHandler fh;

        try {
            fh = new FileHandler("Logs.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return logger;
    }
}
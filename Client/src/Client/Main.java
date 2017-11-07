package Client;

import Commands.CommandsManager;
import Network.Dispatcher;
import Network.NetworkManager;
import Utils.Settings;

import javax.swing.*;
import java.util.logging.*;

public class Main extends JFrame {

    private static Logger logger;
    private static Settings settings;

    private static NetworkManager networkManager;
    private static Dispatcher dispatcher;
    private static CommandsManager commandsManager;

    private JPanel panelMain;
    private JTextField textField1;
    private JButton button1;

    public Main() {
        button1.addActionListener(actionEvent -> {
            if (!textField1.getText().isEmpty()) {
                //test - send file from path entered in textField
                dispatcher.send("ls", 1, "test", textField1.getText());
            }
        });
    }

    public static void main(String[] args) {
        String path = getSettingsPath();

        setUpView();
        logger = setUpLogger();
        settings = new Settings(path + ".SPSClientSettings", logger);
        settings.readSettings();
        commandsManager = new CommandsManager(path, logger);
        networkManager = new NetworkManager(logger);
        networkManager.connect(settings.getIp(), settings.getPort());
        dispatcher = new Dispatcher(networkManager, logger);
    }

    private static void setUpView() {
        JFrame frame = new JFrame("Simple Print Server");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 450);
        frame.setVisible(true);
    }

    private static Logger setUpLogger() {
        Logger logger = Logger.getLogger("SPSClientLogger");
        logger.setUseParentHandlers(false);
        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler("Logs.log", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return logger;
    }

    private static String getSettingsPath(){
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();
        String path;

        if(osNameMatch.contains("linux")) {
            path = System.getProperty("user.home");
        }
        else if(osNameMatch.contains("windows")) {
            path = System.getenv("LOCALAPPDATA") + "/SPServer";
        }
        else {
            path = System.getProperty("user.home");
        }

        return path + "/";
    }
}

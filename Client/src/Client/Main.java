package Client;

import Commands.CommandsManager;
import Network.Dispatcher;
import Network.NetworkManager;
import Utils.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.logging.*;

public class Main extends JFrame {

    private static Logger logger;
    private static Settings settings;

    private static NetworkManager networkManager;
    private static Dispatcher dispatcher;
    private static CommandsManager commandsManager;

    private JPanel panelMain;
    private JTextField filePathField;
    private JButton pathButton;

    public Main(String initPath) {
        pathButton.addActionListener(actionEvent -> choosePath());

        filePathField.setText(initPath);
    }

    public static void main(String[] args) {
        String path = getSettingsPath();

        logger = setUpLogger();
        settings = new Settings(path + ".SPSClientSettings", logger);
        settings.readSettings();
        commandsManager = new CommandsManager(path, logger);
        networkManager = new NetworkManager(logger);
        networkManager.connect(settings.getIp(), settings.getPort());
        dispatcher = new Dispatcher(networkManager, logger);

        EventQueue.invokeLater(Main::setUpView);
    }

    private static void setUpView() {
        JFrame frame = new JFrame("Simple Print Server - client");
        frame.setContentPane(new Main(settings.getLastPath()).panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 450);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
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

    private void choosePath(){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(settings.getLastPathDir()));
        int returnVal = jFileChooser.showOpenDialog(Main.this);

        if(returnVal == JFileChooser.APPROVE_OPTION){
            String path = jFileChooser.getSelectedFile().toString();
            String dir = jFileChooser.getCurrentDirectory().toString();

            filePathField.setText(path);
            settings.setLastPath(path);
            settings.setLastPathDir(dir);
            settings.saveSettings();
        }
    }
}

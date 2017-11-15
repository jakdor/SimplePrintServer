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
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JButton connectButton;
    private JLabel serverStatusLabel;
    private JComboBox configBox;
    private JButton configEditButton;
    private JRadioButton confButtonOpen;
    private JRadioButton confButtonSend;
    private JRadioButton confButtonPrint;
    private JButton SendButton;
    private JLabel actionStatusLabel;

    private final String CONNECTION_TRY = "<html>Status: <font color='orange'>...</font></html>";
    private final String CONNECTION_OK = "<html>Status: <font color='green'>connected</font></html>";
    private final String CONNECTION_FAIL = "<html>Status: <font color='red'>no connection</font></html>";
    private final String CONNECTION_INVALID = "<html>Status: <font color='red'>invalid ip/port</font></html>";
    private final String CONNECTION_CLOSED = "<html>Status: <font color='red'>server shutdown</font></html>";

    private final String TASK_IDL = "No active task/idle";

    public Main(String initPath) {

        pathButton.addActionListener(actionEvent -> choosePath());
        connectButton.addActionListener(actionEvent -> reconnect());

        confButtonPrint.addActionListener(actionEvent -> radioButtonOnlyOne(0));
        confButtonOpen.addActionListener(actionEvent -> radioButtonOnlyOne(1));
        confButtonSend.addActionListener(actionEvent -> radioButtonOnlyOne(2));

        filePathField.setText(initPath);

        serverIpField.setText(settings.getIp());
        serverPortField.setText(Integer.toString(settings.getPort()));

        actionStatusLabel.setText(TASK_IDL);

        connect();
    }

    public static void main(String[] args) {
        String path = getSettingsPath();

        logger = setUpLogger();
        settings = new Settings(path + ".SPSClientSettings", logger);
        settings.readSettings();
        commandsManager = new CommandsManager(path, logger);
        networkManager = new NetworkManager(logger);
        dispatcher = new Dispatcher(networkManager, logger);

        EventQueue.invokeLater(Main::setUpView);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> networkManager.send("@reboot")));
    }

    private static void setUpView() {
        JFrame frame = new JFrame("Simple Print Server - client");
        frame.setContentPane(new Main(settings.getLastPath()).panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 260);
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

            //todo move to validation/send function
            settings.setLastPath(path);
            settings.setLastPathDir(dir);
            settings.saveSettings();
        }
    }

    private void connect(){
        new Thread(() -> {

            if (networkManager.connect(settings.getIp(), settings.getPort())) {
                serverStatusLabel.setText(CONNECTION_OK);

                String serverStatus = networkManager.receive();
                if(serverStatus.equals("ServerShutdown")){
                    serverStatusLabel.setText(CONNECTION_CLOSED);
                    networkManager.disconnect();
                }
                else if(serverStatus.equals("ServerError")){
                    serverStatusLabel.setText(CONNECTION_FAIL);
                    networkManager.disconnect();
                }
            }
            else {
                serverStatusLabel.setText(CONNECTION_FAIL);
            }

        }).start();
    }

    private void reconnect(){
        serverStatusLabel.setText(CONNECTION_TRY);
        new Thread(() ->{

            if(!serverIpField.getText().isEmpty() && !serverPortField.getText().isEmpty()){

                String ip;
                int port;

                try {
                    ip = serverIpField.getText();
                    port = Integer.parseInt(serverPortField.getText());
                }
                catch (Exception e){
                    logger.info("invalid ip/port, " + e.toString());
                    serverStatusLabel.setText(CONNECTION_INVALID);
                    return;
                }

                try {
                    settings.setIp(ip);
                    settings.setPort(port);
                    settings.saveSettings();

                    networkManager.send("@reboot");
                    networkManager.disconnect();
                    connect();
                }
                catch (Exception e){
                    logger.info("error updating server ip/port, " + e.toString());
                }
            }

        }).start();
    }

    private void radioButtonOnlyOne(int num){
        switch (num){
            case 0:
                confButtonPrint.setSelected(true);
                confButtonOpen.setSelected(false);
                confButtonSend.setSelected(false);
                break;
            case 1:
                confButtonPrint.setSelected(false);
                confButtonOpen.setSelected(true);
                confButtonSend.setSelected(false);
                break;
            case 2:
                confButtonPrint.setSelected(false);
                confButtonOpen.setSelected(false);
                confButtonSend.setSelected(true);
                break;
            default:
                confButtonPrint.setSelected(false);
                confButtonOpen.setSelected(false);
                confButtonSend.setSelected(false);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        networkManager.send("@reboot");
    }
}

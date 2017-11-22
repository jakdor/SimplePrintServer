package Client;

import Commands.Command;
import Commands.CommandComboItem;
import Commands.CommandsManager;
import Network.Dispatcher;
import Network.NetworkManager;
import Utils.Observer;
import Utils.Settings;
import Utils.Subject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;

public class Main extends JFrame {

    private static Logger logger;
    private static Settings settings;

    private static NetworkManager networkManager;
    private static Dispatcher dispatcher;
    private static CommandsManager commandsManager;

    private Subject subject;
    private ComboObserver comboObserver;

    private int selectedMode = -1;

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
    private JButton sendButton;
    private JLabel actionStatusLabel;
    private JCheckBox logBox;

    private final String CONNECTION_TRY = "<html>Status: <font color='orange'>...</font></html>";
    private final String CONNECTION_OK = "<html>Status: <font color='green'>connected</font></html>";
    private final String CONNECTION_FAIL = "<html>Status: <font color='red'>no connection</font></html>";
    private final String CONNECTION_INVALID = "<html>Status: <font color='red'>invalid ip/port</font></html>";
    private final String CONNECTION_CLOSED = "<html>Status: <font color='red'>server shutdown</font></html>";

    private final String TASK_IDL = "No active task/idle";
    private final String TASK_SEND = "Task send";
    private final String TASK_FAILED = "<html><font color='red'>Unable to send task</font></html>";
    private final String TASK_INVALID_PATH = "<html><font color='red'>Invalid path</font></html>";
    private final String TASK_INVALID_OPTIONS = "<html><font color='red'>No send option chosen</font></html>";

    //todo auto choosing of profile

    public Main(String initPath) {

        pathButton.addActionListener(actionEvent -> choosePath());
        connectButton.addActionListener(actionEvent -> reconnect());

        confButtonPrint.addActionListener(actionEvent -> radioButtonOnlyOne(0));
        confButtonOpen.addActionListener(actionEvent -> radioButtonOnlyOne(1));
        confButtonSend.addActionListener(actionEvent -> radioButtonOnlyOne(2));

        sendButton.addActionListener(actionEvent -> execute());
        configEditButton.addActionListener(actionEvent -> openEditConfigDialog());

        logBox.setSelected(settings.isLogging());
        logBox.addActionListener(actionEvent -> changeLogging());

        loadModeSetting();

        filePathField.setText(initPath);

        serverIpField.setText(settings.getIp());
        serverPortField.setText(Integer.toString(settings.getPort()));

        actionStatusLabel.setText(TASK_IDL);

        for(Command command : commandsManager) {
            configBox.addItem(new CommandComboItem(command.getName(), commandsManager.indexOf(command)));
        }

        subject = new Subject();
        comboObserver = new ComboObserver(subject);

        connect();
    }

    public static void main(String[] args) {
        String path = getSettingsPath();

        logger = setUpLogger();
        settings = new Settings(path + getSettingsFileName(), logger);
        settings.readSettings();
        commandsManager = new CommandsManager(path, logger);
        networkManager = new NetworkManager(logger);
        dispatcher = new Dispatcher(networkManager, logger);

        commandsManager.readCommands();

        EventQueue.invokeLater(Main::setUpView);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> networkManager.send("@reboot")));
    }

    private static void setUpView() {
        JFrame frame = new JFrame("Simple Print Server - client");
        frame.setContentPane(new Main(settings.getLastPath()).panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 280);
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

    private static String getSettingsFileName(){
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();

        if(osNameMatch.contains("linux")) {
            return ".SPSClientSettings";
        }
        else {
            return "SPSClientSettings";
        }
    }

    private void choosePath(){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(settings.getLastPathDir()));
        int returnVal = jFileChooser.showOpenDialog(Main.this);

        if(returnVal == JFileChooser.APPROVE_OPTION){
            String path = jFileChooser.getSelectedFile().toString();
            filePathField.setText(path);
            autoChooseProfile(path);
        }
    }

    private void autoChooseProfile(String filePath){
        String extension = getFileExtension(filePath);
        int index = commandsManager.getFirstFileFormatIndex(extension);
        configBox.setSelectedIndex(index);
    }

    private String getFileExtension(String filePath) {
        File file = new File(filePath);
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private void changeLogging(){
        settings.setLogging(logBox.isSelected());
        settings.saveSettings();
    }

    private void openEditConfigDialog(){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                EditConfigDialog.start(commandsManager, subject);
            }

            @Override
            protected void finalize() throws Throwable {
                refreshComboBox();
                super.finalize();
            }
        });
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

    private void execute(){
        Command command = commandsManager.get(configBox.getSelectedIndex());

        if(filePathField.getText().isEmpty()){
            actionStatusLabel.setText(TASK_INVALID_PATH);
            return;
        }
        else {
            File file = new File(filePathField.getText());
            if(!file.exists() || file.isDirectory()) {
                actionStatusLabel.setText(TASK_INVALID_PATH);
                return;
            }
        }

        if(selectedMode == -1){
            actionStatusLabel.setText(TASK_INVALID_OPTIONS);
            return;
        }

        Path path = Paths.get(filePathField.getText());

        if(dispatcher.send(command, selectedMode, path.getFileName().toString(), filePathField.getText())){
            settings.setLastPath(path.toString());
            settings.setLastPathDir(path.getParent().toString());
            settings.saveSettings();

            actionStatusLabel.setText(TASK_SEND);
        }
        else {
            actionStatusLabel.setText(TASK_FAILED);
        }
    }

    private void loadModeSetting(){
        switch (settings.getConfigMode()){
            case 0:
                confButtonPrint.setSelected(true);
                break;
            case 1:
                confButtonOpen.setSelected(true);
                break;
            case 2:
                confButtonSend.setSelected(true);
                break;
        }
    }

    private void radioButtonOnlyOne(int num){
        switch (num){
            case 0:
                confButtonPrint.setSelected(true);
                confButtonOpen.setSelected(false);
                confButtonSend.setSelected(false);
                selectedMode = 0;
                break;
            case 1:
                confButtonPrint.setSelected(false);
                confButtonOpen.setSelected(true);
                confButtonSend.setSelected(false);
                selectedMode = 1;
                break;
            case 2:
                confButtonPrint.setSelected(false);
                confButtonOpen.setSelected(false);
                confButtonSend.setSelected(true);
                selectedMode = 2;
                break;
            default:
                confButtonPrint.setSelected(false);
                confButtonOpen.setSelected(false);
                confButtonSend.setSelected(false);
                selectedMode = -1;
        }

        if(selectedMode >= 0){
            settings.setConfigMode(selectedMode);
            settings.saveSettings();
        }
    }

    private void refreshComboBox(){
        configBox.removeAllItems();
        for(Command command : commandsManager) {
            configBox.addItem(new CommandComboItem(command.getName(), commandsManager.indexOf(command)));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        networkManager.send("@reboot");
    }

    private class ComboObserver extends Observer{

        ComboObserver(Subject subject){
            this.subject = subject;
            this.subject.attach(this);
        }

        @Override
        public void update() {
            refreshComboBox();
        }
    }
}

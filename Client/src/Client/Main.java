package Client;

import Model.Command;
import Network.Dispatcher;
import Network.NetworkManager;
import Utils.Settings;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import javax.swing.*;
import java.util.logging.*;

public class Main extends JFrame {

    private static Logger logger;
    private static Settings settings;

    private static NetworkManager networkManager;
    private static Dispatcher dispatcher;

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
        setUpView();

        logger = setUpLogger();

        settings = new Settings(getSettingsPath() + ".SPSClientSettings", logger);
        settings.readSettings();

        initDb();

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

    private static void initDb(){
        String databaseUrl = "jdbc:sqlite:testdb.sqlite";

        ConnectionSource connectionSource = null;

        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
        }
        catch (Exception e){
            logger.info("unable to init local db, " + e.toString());
        }

        if(connectionSource == null){
            return;
        }

        //db test

        try {
            Dao<Command, Integer> commandDao =
                    DaoManager.createDao(connectionSource, Command.class);

            TableUtils.createTableIfNotExists(connectionSource, Command.class);

            String name = "Jim Smith";
            Command command = new Command("ls", 1);

            commandDao.create(command);

            Command command2 = commandDao.queryForId(1);

            System.out.println("test db: " + command2.getCommand() + " | " + Integer.toString(command2.getMode()));

            connectionSource.close();
        }
        catch (Exception e){
            logger.info("test failed" + e.toString());
        }
    }
}

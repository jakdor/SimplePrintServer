package Server;

import Network.NetworkManager;
import Utils.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main
{
    private static boolean mainLoop = true;
    private static Logger logger;

    private static NetworkManager networkManager;
    private static Settings settings;

    private static boolean settingsOpenLock = false;
    private static boolean aboutOpenLock = false;

    public static void main(String[] args) throws Exception
    {
        logger = setUpLogger();

        settings = new Settings(getSettingsPath() + ".SPSSetting");
        settings.readSettings();

        setupSystemTray();

        networkManager = new NetworkManager();
        networkManager.startServer(settings.getPort());

        while(mainLoop)
        {
            try{
                Thread.sleep(10);
            }
            catch (Exception e){
                System.out.println("Error: unable to skip 10ms, " + e.toString());
            }

            networkManager.readMessage();
        }

        System.exit(0);
    }

    private static void setupSystemTray(){
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = createIcon();
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem settingsItem = new MenuItem("Settings");
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");

        settingsItem.addActionListener(listener -> {
            if (!settingsOpenLock) {
                Runnable runnable = () -> {
                    SettingsDialog settingsDialog = new SettingsDialog(settings);
                    settingsDialog.start();
                };

                settingsOpenLock = true;
                new Thread(runnable).run();
            }
        });

        aboutItem.addActionListener(listener -> {
            if(!aboutOpenLock) {
                Runnable runnable = () -> {
                    About about = new About();
                    about.start();
                };

                aboutOpenLock = true;
                new Thread(runnable).run();
            }
        });

        exitItem.addActionListener(listener ->{
            networkManager.shutdownServer();
            mainLoop = false;
        });

        //Add components to pop-up menu
        popup.add(settingsItem);
        popup.add(aboutItem);
        popup.add(exitItem);

        if(trayIcon != null) {
            trayIcon.setPopupMenu(popup);
        }

        try {
            if(trayIcon != null) {
                tray.add(trayIcon);
            }
        } catch (AWTException e) {
            log("TrayIcon could not be added");
        }
    }

    private static TrayIcon createIcon() {
        TrayIcon icon;

        try {
            Image trayIconImage = ImageIO.read(Main.class.getResource("/Assets/icon.png"));
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
            icon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
        }
        catch (Exception e){
            log("Can't load app icon, " + e.toString());
            return null;
        }

        return icon;
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

    public static void log(String msg){
        if(settings.isLogging()){
            logger.info(msg);
        }
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

    static void settingsDialogClosed(){
        Main.settingsOpenLock = false;
    }

    static void aboutDialogClosed(){
        Main.aboutOpenLock = false;
    }
}
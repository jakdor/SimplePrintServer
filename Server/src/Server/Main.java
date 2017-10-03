package Server;

import Network.NetworkManager;
import Utils.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main
{
    private static Logger logger;

    private static NetworkManager networkManager;
    private static Settings settings;

    public static void main(String[] args) throws Exception
    {
        logger = setUpLogger();

        settings = new Settings("SPSSetting");
        settings.readSettings();

        setupSystemTray();

        networkManager = new NetworkManager();
        networkManager.startServer(settings.getPort());

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

        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.add(settingsItem);
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
            log(e.toString());
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
}
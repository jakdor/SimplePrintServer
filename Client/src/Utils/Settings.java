package Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.logging.Logger;

public class Settings {

    private Logger logger;

    private Path fileName;
    private Vector<String> lines;

    private int port;
    private String ip;

    public Settings(String settingsFileName, Logger logger){
        this.logger = logger;
        fileName = Paths.get(settingsFileName);
        lines = new Vector<>();
    }

    public void readSettings(){
        boolean failFlag = false;

        try {
            Files.lines(fileName).forEachOrdered(str -> lines.add(str));
        }
        catch (Exception e){
            logger.info("Unable to read settings file, " + e.toString());
            failFlag = true;
        }

        if(failFlag || lines.isEmpty()){
            setDefaultSettings();
            saveSettings();
            logger.info("Generated default settings file");
        }
        else {
            parseSettings();
        }
    }

    public void saveSettings(){
        lines.removeAllElements();
        lines.add(Integer.toString(port));
        lines.add(ip);

        try {
            Files.write(fileName, lines);
        }
        catch (Exception e){
            logger.info("Unable to save settings file, " + e.toString());
        }
    }

    private void parseSettings(){
        port = Integer.parseInt(lines.get(0));
        ip = lines.get(1);
    }

    private void setDefaultSettings(){
        ip = "127.0.0.1";
        port = 8845;
    }

    public void updateSettings(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String savePath) {
        this.ip = savePath;
    }
}

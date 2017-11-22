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
    private String lastPath;
    private String lastPathDir;
    private boolean logging;
    //todo add send options saving

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
        lines.add(lastPath);
        lines.add(lastPathDir);
        lines.add(Boolean.toString(logging));

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
        lastPath = lines.get(2);
        lastPathDir = lines.get(3);
        logging = Boolean.parseBoolean(lines.get(4));
    }

    private void setDefaultSettings(){
        ip = "127.0.0.1";
        port = 8845;
        lastPath = "";
        lastPathDir = "";
        logging = false;
    }

    public void updateSettings(int port, String ip, String lastPath, String lastPathDir, boolean logging) {
        this.port = port;
        this.ip = ip;
        this.lastPath = lastPath;
        this.lastPathDir = lastPathDir;
        this.logging = logging;
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

    public String getLastPath() {
        return lastPath;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public String getLastPathDir() {
        return lastPathDir;
    }

    public void setLastPathDir(String lastPathDir) {
        this.lastPathDir = lastPathDir;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }
}

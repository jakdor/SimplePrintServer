package Utils;

import Server.Main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class Settings {

    private Path fileName;
    private Vector<String> lines;

    private int port;
    private String savePath;
    private boolean logging;

    public Settings(String settingsFileName){
        fileName = Paths.get(settingsFileName);
        lines = new Vector<>();
    }

    public void readSettings(){
        boolean failFlag = false;

        try {
            Files.lines(fileName).forEachOrdered(str -> lines.add(str));
        }
        catch (Exception e){
            Main.log("Unable to read settings file, " + e.toString());
            failFlag = true;
        }

        if(failFlag || lines.isEmpty()){
            setDefaultSettings();
            saveSettings();
        }
        else {
            parseSettings();
        }
    }

    public void saveSettings(){
        lines.removeAllElements();
        lines.add(Integer.toString(port));
        lines.add(savePath);
        lines.add(Boolean.toString(logging));

        try {
            Files.write(fileName, lines);
        }
        catch (Exception e){
            Main.log("Unable to save settings file, " + e.toString());
        }
    }

    private void parseSettings(){
        port = Integer.parseInt(lines.get(0));
        savePath = lines.get(1);
        logging = Boolean.parseBoolean(lines.get(2));
    }

    private void setDefaultSettings(){
        port = 8845;
        savePath = "";
        logging = true;
    }

    public void updateSettings(int port, String savePath, boolean logging) {
        this.port = port;
        this.savePath = savePath;
        this.logging = logging;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }
}

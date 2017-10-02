import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class Settings {

    private Path fileName;
    private Vector<String> lines;

    private int port;
    private ConfigBundle configBundle;

    Settings(String settingsFileName){
        fileName = Paths.get(settingsFileName);
        lines = new Vector<>();
    }

    void readSettings(){
        try {
            Files.lines(fileName).forEachOrdered(str -> lines.add(str));
        }
        catch (Exception e){
            Main.logger.info("Unable to read settings file, " + e.toString());
            setDefaultSettings();
            saveSettings();
            return;
        }

        parseSettings();
    }

    void saveSettings(){

    }

    private void parseSettings(){

    }

    private void setDefaultSettings(){
        port = 8845;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

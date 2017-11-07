package Commands;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CommandsManager {

    private Logger logger;

    private String commandsSavePath = "";
    private final String FILE_NAME = ".SPSCommands";

    Map<Integer, Command> commandMap = new HashMap<>();

    public CommandsManager(Logger logger){
        this.logger = logger;
        commandsSavePath = getCommandsSavePath();
    }

    public String getCommandsSavePath(){
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

    public void readCommands(String path){
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            commandMap = (Map<Integer, Command>) objectInputStream.readObject();
            objectInputStream.close();
        }
        catch (Exception e){
            logger.info("Error while reading commandsMap, " + e.toString());
        }
    }

    public void writeCommands(String path){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(commandMap);
            objectOutputStream.close();
        }
        catch (Exception e){
            logger.info("Error while saving commandsMap, " + e.toString());
        }
    }

    public Map<Integer, Command> getCommandMap() {
        return commandMap;
    }
}

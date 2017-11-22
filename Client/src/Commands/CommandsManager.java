package Commands;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Logger;

public class CommandsManager implements Iterable<Command> {

    private Logger logger;

    private String commandsSavePath = "";

    private List<Command> commandList = new Vector<>();

    public CommandsManager(String path, Logger logger){
        this.logger = logger;
        this.commandsSavePath = path;

        if(commandsSavePath.isEmpty()){
            commandsSavePath = getCommandFileName();
        }
        else {
            commandsSavePath += "/" + getCommandFileName();
        }
    }

    public void readCommands(){
        try {
            FileInputStream fileInputStream = new FileInputStream(commandsSavePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            commandList = (Vector<Command>) objectInputStream.readObject();
            objectInputStream.close();
        }
        catch (Exception e){
            logger.info("Error while reading commandsList, " + e.toString());
        }
    }

    public void writeCommands(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(commandsSavePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(commandList);
            objectOutputStream.close();
        }
        catch (Exception e){
            logger.info("Error while saving commandsList, " + e.toString());
        }
    }

    public int getFirstFileFormatIndex(String fileFormat){
        for (Command command : commandList){
            if(command.getFileFormat().contains(fileFormat)){
                return commandList.indexOf(command);
            }
        }
        return 0;
    }

    private String getCommandFileName(){
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();

        if(osNameMatch.contains("linux")) {
            return ".SPSCommands";
        }
        else {
            return "SPSCommands";
        }
    }

    public int getIndexWithExtension(String extension){
        for(Command command : commandList){
            if(command.getFileFormat().contains(extension)){
                commandList.indexOf(command);
            }
        }

        return 0;
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    public boolean add(Command command) {
        return commandList.add(command);
    }

    public int size(){
        return commandList.size();
    }

    public Command get(int i) {
        return commandList.get(i);
    }

    public Command set(int i, Command command) {
        return commandList.set(i, command);
    }

    public Command remove(int i) {
        return commandList.remove(i);
    }

    @Override
    public Iterator<Command> iterator() {
        return new CommandIterator();
    }

    public int indexOf(Object o) {
        return commandList.indexOf(o);
    }

    class CommandIterator implements Iterator<Command> {

        private int index = 0;

        public boolean hasNext() {
            return index < size();
        }

        public Command next() {
            return get(index++);
        }

        public void remove() {
            CommandsManager.this.remove(index++);
        }
    }
}

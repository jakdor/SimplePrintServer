package Network;

import Server.Main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskManager {

    private String savePath;
    private String received;

    private String command;
    private String fileStr;
    private String fileName;
    private int mode;

    public TaskManager(String savePath, String received){
        this.savePath = savePath;
        this.received = received;
    }

    public void parse(){
        try {
            int separator1 = received.indexOf("#|#");
            int separator2 = received.indexOf("#!#");
            int separator3 = received.indexOf("#@#");
            command = received.substring(0, separator1);
            mode = Integer.parseInt(received.substring(separator1 + 3, separator2));
            fileName = received.substring(separator2 + 3, separator3);
            fileStr = received.substring(separator3 + 3, received.length());
        }
        catch (Exception e){
            Main.log("Error parsing received string, " + e.toString());
        }
    }

    public void saveFile(){
        if(fileStr.isEmpty()){
            return;
        }

        String path;

        if(savePath.isEmpty()){
            path = fileName;
        }
        else {
            path = savePath;
            if(!savePath.endsWith("/")){
                path += "/";
            }
            path += fileName;
        }

        try{
            if(mode == 0){
                File temp = File.createTempFile("TempFile", ".tmp");
                String tempPath = temp.getAbsolutePath();
                Files.write(Paths.get(tempPath), fileStr.getBytes());
                temp.deleteOnExit();
            }
            else {
                Files.write(Paths.get(path), fileStr.getBytes());
            }
        }
        catch (Exception e){
            Main.log("Unable to save received file, " + e.toString());
        }
    }

    public void execute(){
        switch (mode){
            case 0: //print temp file
                lunchCommand(command);
                break;
            case 1: //open file
                lunchCommand(command);
                break;
            case 2: //received file, let user decide
                break;
        }
    }

    private void lunchCommand(String command){
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();
        ProcessBuilder builder;

        if(osNameMatch.contains("windows")) {
            builder = new ProcessBuilder("cmd.exe", "/c", command);
        }
        else {
            builder = new ProcessBuilder(command);
        }

        builder.redirectErrorStream(true);

        try {
            Process p = builder.start();
        }
        catch (Exception e){
            Main.log("Unable to lunch command, " + e.toString());
        }
    }

    public String getCommand() {
        return command;
    }

    public String getFileStr() {
        return fileStr;
    }

    public int getMode() {
        return mode;
    }

    public String getFileName() {
        return fileName;
    }
}
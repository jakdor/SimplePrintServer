package Network;

import Server.ErrorDialog;
import Server.Main;
import Server.ReceivedDialog;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskManager {

    private String savePath;
    private String received;
    private String tempFilePath = "";

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

    private void insertFileName(){
        if(command.contains("%FILE%")){
            command = command.replace("%FILE%", formatFinalFilePath());
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
                tempFilePath = temp.getAbsolutePath();
                Files.write(Paths.get(tempFilePath), fileStr.getBytes());
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
        Runnable runnable = () -> {

            insertFileName();

            switch (mode) {
                case 0: //print temp file
                    if(!lunchCommand(command)){
                        ErrorDialog.show("Can't lunch print task, try sending file");
                    }
                    break;
                case 1: //open file
                    if(!openFile()){
                        ErrorDialog.show("Can't open received file, something went wrong");
                    }
                    break;
                case 2: //received file, let user decide
                    ReceivedDialog.start(fileName, savePath);
                    break;
            }
        };
        new Thread(runnable).run();
    }

    private boolean lunchCommand(String command){
        boolean result = true;

        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();
        ProcessBuilder builder;

        if(osNameMatch.contains("windows")) {
            builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);

            try {
                builder.start();
            }
            catch (Exception e){
                Main.log("Unable to lunch command, " + e.toString());
                result = false;
            }
        }
        else {
            try {
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    Main.log("wrong command");
                    result = false;
                }
            }
            catch (Exception e){
                Main.log("Unable to lunch command, " + e.toString());
                result = false;
            }
        }

        return result;
    }

    private String formatFinalFilePath(){
        String path = "";
        if(!savePath.isEmpty()){
            path = savePath + "/";
        }
        path += fileName;
        return path;
    }

    private boolean openFile(){

        if(lunchCommand(command)){
            return true;
        }

        try {
            Desktop.getDesktop().open(new File(formatFinalFilePath()));
        }
        catch (Exception e){
            Main.log("Unable to open folder with command nor with system launcher, " + e.toString());
            return false;
        }

        return true;
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

    public String getTempFilePath() {
        return tempFilePath;
    }
}

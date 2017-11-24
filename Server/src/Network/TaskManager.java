package Network;

import Server.ErrorDialog;
import Server.Main;
import Server.ReceivedDialog;

import java.awt.*;
import java.io.*;
import java.util.Base64;

public class TaskManager {

    private String savePath;
    private String received;
    private String tempFilePath = "";

    private String command;
    private byte[] fileData;
    private String fileName;
    private int mode;

    public TaskManager(String savePath, String received){
        this.savePath = savePath;
        this.received = received;
    }

    public Object deserialize(String str) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode(str);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object object  = objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    public void parse(){
        try {
            if(received != null) {
                if(!received.isEmpty()) {
                    Carrier carrier = (Carrier) deserialize(received);
                    command = carrier.getCommand();
                    mode = carrier.getMode();
                    fileName = carrier.getFileName();
                    fileData = carrier.getFileData();
                }
            }
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
        if(fileData == null){
            return;
        }
        else if(fileData.length == 0){
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

                writeToFile(tempFilePath, fileData);

                temp.deleteOnExit();
            }
            else {
                writeToFile(path, fileData);
            }
        }
        catch (Exception e){
            Main.log("Unable to save received file, " + e.toString());
        }
    }

    public void writeToFile(String path, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(data);
            fos.close();
        }
        catch (Exception e){
            Main.log("unable to save file, " + e.toString());
        }
    }

    public void execute(){
        if(command == null){
            return;
        }
        else if(command.isEmpty()){
            return;
        }

        Runnable runnable = () -> {

            insertFileName();

            switch (mode) {
                case 0: //print temp file/lunch command
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
                    EventQueue.invokeLater(() ->  ReceivedDialog.start(fileName, savePath));
                    break;
            }
        };
        new Thread(runnable).run();
    }

    private boolean lunchCommand(String command){

        if(command.isEmpty()){
            return false;
        }

        boolean result = true;

        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();

        if(osNameMatch.contains("windows")) {
            try {
                Runtime.getRuntime().exec(command);
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

        if(mode == 0){
            path = tempFilePath;
        }
        else {
            String osName = System.getProperty("os.name");
            String osNameMatch = osName.toLowerCase();

            String slash;
            if(osNameMatch.contains("windows")) {
                slash = "\\";
            }
            else {
                slash = "/";
            }

            if(!savePath.isEmpty()){
                path = savePath + slash;
            }
            path += fileName;
        }

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

    public byte[] getFileData() {
        return fileData;
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

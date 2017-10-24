package Network;

import java.io.Serializable;

public class Carrier implements Serializable {

    private String command;
    private String fileStr;
    private String fileName;
    private int mode;

    public Carrier(){
    }

    public Carrier(String command, int mode, String fileName, String fileStr) {
        this.command = command;
        this.fileStr = fileStr;
        this.fileName = fileName;
        this.mode = mode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFileStr() {
        return fileStr;
    }

    public void setFileStr(String fileStr) {
        this.fileStr = fileStr;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

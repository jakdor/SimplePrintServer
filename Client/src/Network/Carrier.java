package Network;

import java.io.Serializable;
import java.util.Arrays;

public class Carrier implements Serializable {

    private String command;
    private byte[] fileData;
    private String fileName;
    private int mode;

    public Carrier(){
    }

    public Carrier(String command, int mode, String fileName, byte[] fileData) {
        this.command = command;
        this.fileData = fileData;
        this.fileName = fileName;
        this.mode = mode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Carrier carrier = (Carrier) o;

        if (mode != carrier.mode) return false;
        if (command != null ? !command.equals(carrier.command) : carrier.command != null) return false;
        if (!Arrays.equals(fileData, carrier.fileData)) return false;
        return fileName != null ? fileName.equals(carrier.fileName) : carrier.fileName == null;
    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(fileData);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + mode;
        return result;
    }
}

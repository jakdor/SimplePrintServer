public class Config{

    private Long id;
    private String command;
    private String fileType;
    private boolean sendFile;

    public Config(){
    }

    public Config(Long id, String command, String fileType, boolean sendFile) {
        this.id = id;
        this.command = command;
        this.fileType = fileType;
        this.sendFile = sendFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isSendFile() {
        return sendFile;
    }

    public void setSendFile(boolean sendFile) {
        this.sendFile = sendFile;
    }
}

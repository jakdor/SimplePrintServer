package Commands;

import java.io.Serializable;

public class Command implements Serializable {

    private String name;
    private String printCommand;
    private String openCommand;
    private String fileFormat;

    Command(){
    }

    public Command(String name, String printCommand, String openCommand, String fileFormat) {
        this.name = name;
        this.printCommand = printCommand;
        this.openCommand = openCommand;
        this.fileFormat = fileFormat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrintCommand() {
        return printCommand;
    }

    public void setPrintCommand(String printCommand) {
        this.printCommand = printCommand;
    }

    public String getOpenCommand() {
        return openCommand;
    }

    public void setOpenCommand(String openCommand) {
        this.openCommand = openCommand;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Command command = (Command) object;

        if (name != null ? !name.equals(command.name) : command.name != null) {
            return false;
        }
        if (printCommand != null ? !printCommand.equals(command.printCommand) : command.printCommand != null) {
            return false;
        }
        if (openCommand != null ? !openCommand.equals(command.openCommand) : command.openCommand != null) {
            return false;
        }
        return fileFormat != null ? fileFormat.equals(command.fileFormat) : command.fileFormat == null;
    }

}

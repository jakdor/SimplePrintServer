package Commands;

import java.io.Serializable;

public class Command implements Serializable {

    private String name;
    private String printCommand;
    private String openCommand;

    Command(){

    }

    public Command(String name, String printCommand, String openCommand) {
        this.name = name;
        this.printCommand = printCommand;
        this.openCommand = openCommand;
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
}

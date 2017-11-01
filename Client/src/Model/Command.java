package Model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "commands")
public class Command {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "command")
    private String command;

    @DatabaseField(columnName = "mode")
    private int mode;

    Command(){

    }

    public Command(String command, int mode) {
        this.command = command;
        this.mode = mode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

package Client;

import Commands.Command;
import Commands.CommandComboItem;
import Commands.CommandsManager;
import Utils.Subject;

import javax.swing.*;

public class EditConfigDialog extends JDialog {

    private static CommandsManager commandsManager;
    private static Subject subject;

    private boolean addNewActive = false;

    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox configBox;
    private JButton newButton;
    private JTextField nameTextField;
    private JTextField printTextField;
    private JTextField openTextField;
    private JTextField formatTextField;
    private JButton saveButton;
    private JButton deleteButton;

    private EditConfigDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(actionEvent -> onOK());
        newButton.addActionListener(actionEvent -> addNewCommand());
        saveButton.addActionListener(actionEvent -> save());
        deleteButton.addActionListener(actionEvent -> delete());

        refreshComboBox(0);
        configBox.addActionListener(actionEvent -> updateEditFields(configBox.getSelectedIndex()));

        updateEditFields(0);
    }

    private void onOK() {
        dispose();
    }

    static void start(CommandsManager commandsManager, Subject subject) {
        EditConfigDialog.commandsManager = commandsManager;
        EditConfigDialog.subject = subject;

        EditConfigDialog dialog = new EditConfigDialog();
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setSize(500, dialog.getHeight());
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    @Override
    public void dispose() {
        subject.notifyAllObservers();
        super.dispose();
    }

    private void refreshComboBox(int startIndex){
        configBox.removeAllItems();
        for(Command command : commandsManager) {
            configBox.addItem(new CommandComboItem(command.getName(), commandsManager.indexOf(command)));
        }
        if(startIndex < commandsManager.size()) {
            configBox.setSelectedIndex(startIndex);
        }
    }

    private void updateEditFields(int index){
        if(index < 0){
            return;
        }
        nameTextField.setText(commandsManager.get(index).getName());
        printTextField.setText(commandsManager.get(index).getPrintCommand());
        openTextField.setText(commandsManager.get(index).getOpenCommand());
        formatTextField.setText(commandsManager.get(index).getFileFormat());
    }

    private void addNewCommand(){
        nameTextField.setText("");
        printTextField.setText("");
        openTextField.setText("");
        formatTextField.setText("");
        addNewActive = true;
    }

    private void delete(){
        if(!addNewActive){
            commandsManager.remove(configBox.getSelectedIndex());
            commandsManager.writeCommands();
        }
        refreshComboBox(0);
    }

    private void save(){
        if(addNewActive){
            commandsManager.add(new Command(nameTextField.getText(),
                    printTextField.getText(), openTextField.getText(), formatTextField.getText()));
            refreshComboBox(commandsManager.size()-1);
            addNewActive = false;
        }
        else {
            commandsManager.set(configBox.getSelectedIndex(), new Command(nameTextField.getText(),
                    printTextField.getText(), openTextField.getText(), formatTextField.getText()));
            refreshComboBox(configBox.getSelectedIndex());
        }
        commandsManager.writeCommands();
    }
}

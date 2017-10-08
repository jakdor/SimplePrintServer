package Server;

import Utils.Settings;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField filePathField;
    private JButton choosePathButton;
    private JTextField serverPortField;
    private JRadioButton loggingButton1;
    private JRadioButton loggingButton2;
    private JRadioButton receivedButton1;
    private JRadioButton receivedButton2;
    private JLabel label1;
    private JLabel label2;
    private JLabel label4;
    private JLabel label3;
    private JLabel label5;

    private Settings settings;

    private SettingsDialog(Settings settings) {
        this.settings = settings;

        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setTitle("SimplePrintServer Settings");
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Main.settingsDialogClosed();
                super.windowClosed(windowEvent);
            }
        });

        loadSettings();

        buttonOK.addActionListener(listener -> onOK());
        choosePathButton.addActionListener(listener -> choosePath());

        loggingButton1.addActionListener(listener -> {
            loggingButton1.setSelected(true);
            loggingButton2.setSelected(false);
        });
        loggingButton2.addActionListener(listener -> {
            loggingButton2.setSelected(true);
            loggingButton1.setSelected(false);
        });

        receivedButton1.addActionListener(listener -> {
            receivedButton1.setSelected(true);
            receivedButton2.setSelected(false);
        });
        receivedButton2.addActionListener(listener ->{
            receivedButton2.setSelected(true);
            receivedButton1.setSelected(false);
        });
    }

    private void onOK() {
        updateSettings();
        Main.settingsDialogClosed();
        dispose();
    }

    private void choosePath(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(settings.getSavePath()));
        chooser.setDialogTitle("Choose save folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().toString());
        }
    }

    private void updateSettings(){
        int port = settings.getPort();
        String savePath = settings.getSavePath();
        boolean logging = settings.isLogging();
        boolean openOptions = settings.isOpenOptions();

        try {
            port = Integer.parseInt(serverPortField.getText());
            if(port < 0){
                port = settings.getPort();
            }
        }
        catch (Exception e){
            Main.log("Invalid port, " + e.toString());
        }

        try {
            savePath = filePathField.getText();
        }
        catch (Exception e){
            Main.log("Invalid save path, " + e.toString());
        }

        try {
            logging = loggingButton1.isSelected();
        }
        catch (Exception e){
            Main.log("Invalid logging option, " + e.toString());
        }

        try {
            openOptions = receivedButton1.isSelected();
        }
        catch (Exception e){
            Main.log("Invalid open option, " + e.toString());
        }

        settings.updateSettings(port, savePath, openOptions, logging);
        settings.saveSettings();
    }

    private void loadSettings(){
        filePathField.setText(settings.getSavePath());
        serverPortField.setText(Integer.toString(settings.getPort()));
        if(settings.isOpenOptions()){
            receivedButton1.setSelected(true);
            receivedButton2.setSelected(false);
        }
        else {
            receivedButton1.setSelected(false);
            receivedButton2.setSelected(true);
        }

        if(settings.isLogging()){
            loggingButton1.setSelected(true);
            loggingButton2.setSelected(false);
        }
        else {
            loggingButton1.setSelected(false);
            loggingButton2.setSelected(true);
        }
    }

    static void start(Settings settings){
        SettingsDialog dialog = new SettingsDialog(settings);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

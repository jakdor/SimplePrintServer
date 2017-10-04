package Server;

import Utils.Settings;

import javax.swing.*;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField filePathField;
    private JLabel label1;
    private JButton choosePathButton;
    private JLabel label2;
    private JTextField serverPortField;
    private JLabel label4;
    private JLabel label3;
    private JRadioButton loggingButton1;
    private JRadioButton loggingButton2;
    private JLabel label5;
    private JRadioButton receivedButton1;
    private JRadioButton receivedButton2;
    private JButton buttonCancel;

    private Settings settings;

    SettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(listener -> onOK());

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
        dispose();
        updateSettings();
    }

    private void updateSettings(){
        int port = Integer.parseInt(serverPortField.getText());
        String savePath = filePathField.getText();
        boolean logging = loggingButton1.isSelected();
        boolean openOptions = receivedButton1.isSelected();

        if(port <= 0){
            port = 8845;
        }

        //todo validate
        settings.updateSettings(port, savePath, openOptions, logging);
    }

    void start(Utils.Settings settings){
        this.settings = settings;

        SettingsDialog dialog = new SettingsDialog();
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

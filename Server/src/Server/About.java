package Server;

import javax.swing.*;

public class About extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea aboutInfoStuffTextArea;

    About() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setTitle("About SimplePrintServer");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(listener -> onOK());
    }

    private void onOK() {
        dispose();
    }

    void start() {
        About dialog = new About();
        dialog.setSize(350,200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

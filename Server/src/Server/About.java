package Server;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class About extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea aboutInfoStuffTextArea; //todo add project info

    private About() {
        setContentPane(contentPane);
        setModal(true);
        setResizable(false);
        setTitle("About SimplePrintServer");
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Main.aboutDialogClosed();
                super.windowClosed(windowEvent);
            }
        });

        buttonOK.addActionListener(listener -> onOK());
    }

    private void onOK() {
        Main.aboutDialogClosed();
        dispose();
    }

    static void start() {
        About dialog = new About();
        dialog.setSize(350,200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

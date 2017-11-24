package Server;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class About extends JDialog {

    private static Logger logger;

    private JPanel contentPane;
    private JButton buttonOK;
    private JTextPane aboutInfoStuffTextArea; //todo add project info

    private final String INFO = "<html>Open source project under MIT license <br> by Jakub Dorda 2017 <br><br>" +
            "Project git repo:<br><a href=\"https://github.com/jakdor/SimplePrintServer\">" +
            "github.com/jakdor/SimplePrintServer</a></html>";

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

        aboutInfoStuffTextArea.setEditable(false);
        aboutInfoStuffTextArea.setOpaque(false);
        aboutInfoStuffTextArea.setContentType("text/html");
        aboutInfoStuffTextArea.setText(INFO);
        aboutInfoStuffTextArea.addHyperlinkListener(hyperlinkEvent -> {
            if(hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
                    } catch (Exception e) {
                        logger.info("unable to open link, " + e.toString());
                    }
                }
            }
        });
    }

    private void onOK() {
        Main.aboutDialogClosed();
        dispose();
    }

    static void start(Logger logger) {
        About.logger = logger;
        About dialog = new About();
        dialog.setSize(350,200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

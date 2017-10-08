package Server;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ErrorDialog {

    public static void show(String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
                JOptionPane.ERROR_MESSAGE);
    }
}
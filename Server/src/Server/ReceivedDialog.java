package Server;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReceivedDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOpenFile;
    private JButton buttonOpenFolder;
    private JLabel label1;

    private String fileName;
    private String filePath;

    private ReceivedDialog(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;

        setContentPane(contentPane);
        setModal(true);
        setTitle(fileName);
        getRootPane().setDefaultButton(buttonOpenFile);

        buttonOpenFile.addActionListener(listener -> openFile());
        buttonOpenFolder.addActionListener(listener -> openFolder());

        setMessage();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setMessage(){
        String str = "<html>Received new file: " + fileName + "<br><br>Saved to: " + filePath + "</html>";
        label1.setText(str);
    }

    private void openFile() {
        try {
            String path = "";
            if(!filePath.isEmpty()){
                path = filePath + "/";
            }
            path += fileName;

            Desktop.getDesktop().open(new File(path));
        }
        catch (Exception e){
            Main.log("Unable to open folder, " + e.toString());
        }

        dispose();
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(new File(filePath));
        }
        catch (Exception e){
            Main.log("Unable to open folder, " + e.toString());
        }

        dispose();
    }

    public static void start(String fileName, String filePath) {
        ReceivedDialog dialog = new ReceivedDialog(fileName, filePath);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

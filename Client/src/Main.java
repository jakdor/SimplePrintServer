import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.*;

public class Main extends JFrame {

    static Logger logger;

    private static NetworkManager networkManager;

    private JPanel panelMain;
    private JTextField textField1;
    private JButton button1;

    public Main() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!textField1.getText().isEmpty()){
                    networkManager.send(textField1.getText());
                }
            }
        });
    }

    public static void main(String[] args) {
       setUpView();
       logger = setUpLogger();
       networkManager = new NetworkManager();
       networkManager.connect("127.0.0.1", 8845);
    }

    private static void setUpView(){
        JFrame frame = new JFrame("Simple Print Server");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 450);
        frame.setVisible(true);
    }

    private static Logger setUpLogger(){
        Logger logger = Logger.getLogger("SPSClientLogger");
        logger.setUseParentHandlers(false);
        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler("Logs.log", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return logger;
    }
}

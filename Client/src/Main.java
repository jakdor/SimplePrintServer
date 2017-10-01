import javax.swing.*;

public class Main extends JFrame {
    private JPanel panelMain;

    public static void main(String[] args) {
       JFrame frame = new JFrame("Simple Print Server");
       frame.setContentPane(new Main().panelMain);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(650, 450);
       frame.setVisible(true);
    }
}

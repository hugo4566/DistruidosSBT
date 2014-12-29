import javax.swing.*;
import java.awt.*;

/**
 * Created by Hugo on 29/12/2014.
 */
public class gui {
    private JTextField serverField;
    private JTextField senhaField;
    private JTextField loginField;
    private JButton conectarButton;
    private JPanel panel;
    private JLabel statusLabel;

    public gui() {
        conectarButton.addActionListener(e -> {
            System.out.print("OI");
            statusLabel.setText("CONECTADO!");
            statusLabel.setForeground(Color.GREEN);
        });
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("gui");
        frame.setContentPane(new gui().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

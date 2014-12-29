import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Hugo on 29/12/2014.
 */
public class gui {
    SimpleFTP simpleFTP = new SimpleFTP();
    private JTextField serverField;
    private JTextField senhaField;
    private JTextField loginField;
    private JButton conectarButton;
    private JPanel panel;
    private JLabel statusLabel;

    public gui() {
        serverField.setText("ftp.xpg.com.br");
        loginField.setText("hugo4566");
        senhaField.setText("teste123");

        conectarButton.addActionListener(e -> {
            try {
                simpleFTP.connect(serverField.getText(), loginField.getText(),senhaField.getText());
                statusLabel.setText("CONECTADO!");
                statusLabel.setForeground(Color.GREEN);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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

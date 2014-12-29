import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Hugo on 29/12/2014.
 */
public class gui {
    SimpleFTP simpleFTP = new SimpleFTP();
    private JTextField serverField;
    private JPasswordField senhaField;
    private JTextField loginField;
    private JButton conectarButton;
    private JPanel panel;
    private JLabel statusLabel;
    private JTable table;

    public gui() {
        serverField.setText("ftp.xpg.com.br");
        loginField.setText("hugo4566");
        senhaField.setText("teste123");
        int ultimoCod = 1;

        ArrayList listaFTP = new ArrayList();

        //cria o modelo de Produto
        FileFTPTableModel model = new FileFTPTableModel(listaFTP);

        //atribui o modelo à tabela
        table.setModel(model);

        conectarButton.addActionListener(e -> {
            try {
                statusLabel.setText(simpleFTP.connect(serverField.getText(), loginField.getText(), new String(senhaField.getPassword())));
                //simpleFTP.nlst();
                //simpleFTP.mkd("dir_teste");
                model.addAll(simpleFTP.list());
                table.updateUI();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("gui");
        frame.setContentPane(new gui().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

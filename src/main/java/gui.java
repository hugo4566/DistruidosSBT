import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        ArrayList listaFTP = new ArrayList();
        FileFTPTableModel model = new FileFTPTableModel(listaFTP);
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

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
                    System.out.println("You right clicked on the button");
                    JMenuItem menuItem = new JMenuItem("A popup menu item");
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(menuItem);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                    int[] rows = table.getSelectedRows();
                    List fileFTPs = model.getObjectsRows(rows);
                    System.out.println(fileFTPs.toString());
                }
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

    public class InteractiveTableModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                int column = evt.getColumn();
                int row = evt.getFirstRow();
                System.out.println("row: " + row + " column: " + column);
                table.setColumnSelectionInterval(column + 1, column + 1);
                table.setRowSelectionInterval(row, row);
            }
        }
    }
}

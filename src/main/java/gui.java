import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.*;
import java.io.File;
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
    private JButton escolherPastaButton;
    private JTable table1;

    public gui() {
        serverField.setText("ftp.xpg.com.br");
        loginField.setText("hugo4566");
        senhaField.setText("teste123");

        ArrayList listaFTP = new ArrayList();
        FileFTPTableModel model = new FileFTPTableModel(listaFTP);
        model.addTableModelListener(new InteractiveTableModelListener());
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
                    int[] rows = table.getSelectedRows();
                    List fileFTPs = model.getObjectsRows(rows);
                    System.out.println(fileFTPs.toString());
                    System.out.println("You right clicked on the button");
                    JMenuItem menuItem = new JMenuItem("Delete Selected");
                    menuItem.addActionListener(ac ->{
                        for (int i = 0; i < fileFTPs.size(); i++) {
                            try {
                                simpleFTP.dele(((FileFTP) fileFTPs.get(i)).getNome());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                    });
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(menuItem);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        escolherPastaButton.addActionListener(ac -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Pasta para sincronizar");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //
            // disable the "All files" option.
            //
            chooser.setAcceptAllFileFilterUsed(false);
            //
            if (chooser.showOpenDialog(escolherPastaButton.getComponentPopupMenu()) == JFileChooser.APPROVE_OPTION) {
                System.out.println("getCurrentDirectory(): "
                        +  chooser.getCurrentDirectory());
                System.out.println("getSelectedFile() : "
                        +  chooser.getSelectedFile());


                File folder = new File(""+chooser.getSelectedFile());
                File[] listOfFiles = folder.listFiles();

                ArrayList listaFTP2 = new ArrayList();
                FileFTPTableModel model2 = new FileFTPTableModel(listaFTP2);
                model2.addTableModelListener(new InteractiveTableModelListener());
                table1.setModel(model2);


                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        System.out.println("File " + listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                    model2.addRow(listOfFiles[i]);
                }


            }
            else {
                System.out.println("No Selection ");
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

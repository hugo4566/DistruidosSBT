import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugo on 29/12/2014.
 */
public class FileFTPTableModel extends AbstractTableModel {

    private List files;

    public FileFTPTableModel() {
        files = new ArrayList();
    }

    public FileFTPTableModel(List lista) {
        this();
        files.addAll(lista);
    }


    public List getObjectsRows(int[] numRows){
        ArrayList<FileFTP> tempFiles = new ArrayList();
        for (int i = 0; i < numRows.length; i++) {
            tempFiles.add((FileFTP) files.get(numRows[i]));
        }
        return tempFiles;
    }

    public int getRowCount() {
        return files.size();
    }

    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0: return "Nome";
            case 1: return "Tamanho";
            case 2: return "Modificado";
            default: return "";
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return String.class;
            case 1: return Integer.class;
            case 2: return String.class;
            default: return String.class;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        FileFTP f = (FileFTP) files.get(rowIndex);
        switch (columnIndex) {
            case 0: return "<html><font color=\""+(f.getTipo() == 1 ? "green\">" : "orange\">")+f.getNome()+"</font><html/>";
            case 1: return f.getTamanho();
            case 2: return f.getModificado();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addRow(FileFTP fileFTP) {
        files.add(fileFTP);
    }

    public void addAll(ArrayList filesFTP) {
        for (int i = 0; i < filesFTP.size() ; i++) {
            files.add(filesFTP.get(i));
        }
    }
}

package red.medusa.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/24 周二
 */
public class SegmentTableModel extends AbstractTableModel {
    private List<Object[]> segmentObjects;

    private final String[] n = {"标题", "模块","ID"};

    public SegmentTableModel(List<Object[]> segmentObjects) {
        this.segmentObjects = segmentObjects;
    }

    public int getColumnCount() {
        return n.length;
    }

    public int getRowCount() {
        return segmentObjects.size();
    }

    public String getColumnName(int col) {
        return n[col];
    }

    public Object getValueAt(int row, int col) {
        return segmentObjects.get(row)[col];
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) { // 判断单元格是否可以编辑
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        segmentObjects.get(row)[col] = value;

        fireTableCellUpdated(row, col);
    }

    public void changeModule(List<Object[]> segmentObjects) {
        this.segmentObjects = segmentObjects;
        fireTableDataChanged();
    }

    public void removeRow(int row){
        this.segmentObjects.remove(row);
        fireTableDataChanged();
    }
}

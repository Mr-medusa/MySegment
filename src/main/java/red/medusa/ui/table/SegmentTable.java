package red.medusa.ui.table;


import com.intellij.ui.table.JBTable;
import red.medusa.ui.segment_action.SegmentAction;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/24 周二
 */
public class SegmentTable extends JBTable {
    public static final int ID_COLUMN = 3;
    private final SegmentTableModel tableModel = new SegmentTableModel(new ArrayList<>());

    public SegmentTable() {
        this.setModel(tableModel);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        this.setFillsViewportHeight(true);

        TableColumnModel tcm = this.getColumnModel();
        TableColumn tc = tcm.getColumn(ID_COLUMN);
        this.removeColumn(tc);//隐藏某列
    }

    public void refresh() {
        tableModel.changeModule(fillModule());
    }

    public List<Object[]> fillModule() {
        return new SegmentAction().list();
    }

    public SegmentTableModel getSegmentTableModel(){
        return tableModel;
    }


}













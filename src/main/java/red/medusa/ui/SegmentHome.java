package red.medusa.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import red.medusa.service.entity.Module;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Version;
import red.medusa.ui.action.*;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.segment_action.ModuleAction;
import red.medusa.ui.segment_action.QueryAction;
import red.medusa.ui.controls.table.SegmentTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


/**
 * @author huguanghui
 * @since 2020/11/24 周二
 */
@Slf4j
public class SegmentHome extends SegmentSwitchableBranchDialog {

    private final JPanel bar = new JPanel();
    private final JPanel header = new JPanel();
    private final JPanel content = new JPanel();
    private final JPanel footer = new JPanel();

    private final static String MODULE_VERSION_FIRST_SELECT = " -请选择- ";
    private final ComboBox<Module> modelComboBox = new ComboBox<>();
    private final ComboBox<Version> versionComboBox = new ComboBox<>();
    private final JTextField keyWord = new JTextField();
    private final JButton refresh = new JButton("刷新");

    private final SegmentTable table = new SegmentTable();
    private final JBScrollPane scrollPaneForTable = new JBScrollPane(table);
    private final GridBagLayout gridBag = new GridBagLayout();

    /*
        search
     */
    private final QueryAction queryAction = new QueryAction(table);
    private ModuleListener moduleListener;
    private VersionListener versionListener;
    private KeywordListener keywordListener;

    public SegmentHome() {
        initControlsProp();
        initControlsEvent();
        initFrame();
        fillFrameContent();

        addControlsEventLister();

        registerKeyboardAction();
    }

    private void initControlsProp() {
//        table.setToolTipText("press ctrl+mouse");
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initFrame() {
        bar.setLayout(new BorderLayout());
        bar.add(header, BorderLayout.NORTH);
        bar.add(content, BorderLayout.CENTER);
        bar.add(footer, BorderLayout.SOUTH);

        header.setLayout(new GridLayout(1, 2));
        content.setLayout(new BorderLayout());
        footer.setLayout(gridBag);
    }

    private void fillFrameContent() {
        // header
        header.add(modelComboBox);
        header.add(versionComboBox);
        // content
        content.add(scrollPaneForTable, BorderLayout.CENTER);
        // footer
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.weightx = 0.85;
        gridBag.setConstraints(keyWord, c);
        footer.add(keyWord);

        c.weightx = 0.15;
        gridBag.setConstraints(refresh, c);
        footer.add(refresh);

    }

    private void initControlsEvent() {
        // 修改值并回车有用
        // table.getModel().addTableModelListener();
        // 左键按下|释放/右键按下
        //  table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        //      @Override
        //      public void valueChanged(ListSelectionEvent e) {
        //          // 区分鼠标按下|释放
        //          if (e.getValueIsAdjusting()) {
        //              DefaultListSelectionModel source = (DefaultListSelectionModel) e.getSource();
        //              int selectionIndex = source.getAnchorSelectionIndex();
        //              Object valueAt = table.getModel().getValueAt(selectionIndex, 0);
        //              System.out.println(valueAt);
        //          }
        //      }
        //  });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint());        //获得行位置
                // int col = ((JTable) e.getSource()).columnAtPoint(e.getPoint());     //获得列位置
                try {
                    Object o = table.getModel().getValueAt(row, SegmentTable.ID_COLUMN);   //获得点击单元格数据

                    if (e.getButton() == 1 && e.isControlDown()) {
                        SegmentContextHolder.setSegment((Segment) new Segment().setId(Long.parseLong(o.toString())));
                        new SegmentDetailDialog().show();
                    } else if (e.getButton() == 3) {
                        SegmentContextHolder.setSegment((Segment) new Segment().setId(Long.parseLong(o.toString())));
                        popupInvoked(e.getComponent(), e.getPoint().x, e.getPoint().y, row);
                    }
                } catch (IndexOutOfBoundsException ignore) {
                }
            }
        });
        refresh.addActionListener(e -> refresh2());
    }

    public void addControlsEventLister() {
        keywordListener = new KeywordListener();
        moduleListener = new ModuleListener();
        versionListener = new VersionListener();

        keyWord.getDocument().addDocumentListener(keywordListener);
        modelComboBox.addItemListener(moduleListener);
        versionComboBox.addItemListener(versionListener);
    }

    public void removeControlsEventLister() {
        keyWord.getDocument().removeDocumentListener(keywordListener);
        modelComboBox.removeItemListener(moduleListener);
        versionComboBox.removeItemListener(versionListener);
    }


    @Override
    public JComponent getJComponent() {
        return bar;
    }


    /*
        加载数据
     */
    public void refresh2() {
        removeControlsEventLister();

        /*
            模块 版本
         */
        List<Module> modules = new ModuleAction().list();
        modelComboBox.removeAllItems();
        versionComboBox.removeAllItems();
        modelComboBox.addItem(new Module().setName(MODULE_VERSION_FIRST_SELECT));
        versionComboBox.addItem(new Version().setName(MODULE_VERSION_FIRST_SELECT));
        for (Module module : modules) {
            modelComboBox.addItem(module);
        }
        if (modules.size() > 0) {
            List<Version> versions = modules.get(0).getVersions();
            if (versions != null) {
                if (!versions.isEmpty()) {
                    for (Version version : versions) {
                        versionComboBox.addItem(version);
                    }
                }
            }
        }

        SegmentContextHolder.setSegment(new Segment());
        table.refresh();

        addControlsEventLister();
    }

    /*
        search
     */
    private class VersionListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Module module = (Module) modelComboBox.getSelectedItem();
                Version version = (Version) versionComboBox.getSelectedItem();
                if (module.getName().equals(MODULE_VERSION_FIRST_SELECT)) {
                    refresh2();
                    return;
                }
                if (version.getName().equals(MODULE_VERSION_FIRST_SELECT)) {
                    queryAction.queryByModule(module.getId());
                    return;
                }
                queryAction.queryByVersion(module.getId(), version.getId());
            }
        }
    }

    private class ModuleListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Module module = (Module) modelComboBox.getSelectedItem();
                if (module.getName().equals(MODULE_VERSION_FIRST_SELECT)) {
                    refresh2();
                    return;
                }

                versionComboBox.removeAllItems();
                versionComboBox.addItem(new Version().setName(MODULE_VERSION_FIRST_SELECT));

                if (module.getVersions() != null)
                    for (Version version : module.getVersions()) {
                        versionComboBox.addItem(version);
                    }
                queryAction.queryByModule(module.getId());
            }
        }
    }

    private class KeywordListener extends DocumentAdapter {
        @Override
        protected void textChanged(@NotNull DocumentEvent e) {
            if (keyWord.getText().trim().length() == 0) {
                refresh2();
            } else {
                queryAction.queryByKeyword(keyWord.getText().trim());
            }
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      Pop Action            ++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void popupInvoked(final Component comp, final int x, final int y, int row) {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new PopupDetailPopupSegmentAction());
        group.add(new PopupDetailSegmentAction());
        group.add(new PopupAddSegmentAction());
        group.add(new PopupEditSegmentAction());
        group.add(new PopupDelSegmentAction(this.table.getSegmentTableModel(), row));

        final ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.ANT_EXPLORER_POPUP, group);
        popupMenu.getComponent().show(comp, x, y);
    }

}

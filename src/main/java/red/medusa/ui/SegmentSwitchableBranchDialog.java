package red.medusa.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.service.service.SegmentEntityService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huguanghui
 * @since 2020/12/07 周一
 */
@Slf4j
public abstract class SegmentSwitchableBranchDialog implements SegmentComponent {
    private String dbName = "";
    private boolean isNeedSwitch;

    public void registerKeyboardAction() {
        getJComponent().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (new SwitchBranchDialog().showAndGet() && isNeedSwitch) {
                    doSwitchBranch();
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        getJComponent().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SegmentEntityService entityService = SegmentEntityService.getInstance();
                entityService.finishService();
                if (entityService.isClose()) {
                    NotifyUtils.notifyInfo("数据库已断开连接...");
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        getJComponent().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SegmentEntityService entityService = SegmentEntityService.getInstance();
                entityService.recreateEntityManagerFactory();
                if (entityService.isOpen()) {
                    NotifyUtils.notifyInfo("数据库已建立连接...");
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void doSwitchBranch() {
        if (this.isNeedSwitch) {
            AppSettingsState appSettingsState = AppSettingsState.getInstance();

            if (!this.dbName.isEmpty() && !appSettingsState.dbName.equals(this.dbName))
                appSettingsState.dbName = this.dbName;

            SegmentEntityService.getInstance().recreateEntityManagerFactory();

            isNeedSwitch = false;
        }
    }

    private class SwitchBranchDialog extends DialogWrapper {
        private final AppSettingsState appSettingsState = AppSettingsState.getInstance();

        public SwitchBranchDialog() {
            super(true); // use current window as parent
            init();
            setTitle("切换DB");
            setResizable(true);
        }

        @Override
        protected JComponent createCenterPanel() {
            Box dialogPanel = Box.createVerticalBox();
            ComboBox<String> dbNameComboBox = new ComboBox<>();
            dbNameComboBox.setPreferredSize(new Dimension(300,30));

            Box hbox02 = Box.createHorizontalBox();
            hbox02.add(new JLabel("文件名: "));
            hbox02.add(dbNameComboBox);
            dialogPanel.add(hbox02);

            try {
                List<String> dbNames = findDbFileName();
                for (int j = 0; j < dbNames.size(); j++) {
                    dbNameComboBox.addItem(dbNames.get(j));
                    if (appSettingsState.dbName.equals(dbNames.get(j)))
                        dbNameComboBox.setSelectedIndex(j);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            dbNameComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        dbName = (String) e.getItem();
                        isNeedSwitch = true;
                    }
                }
            });
            return dialogPanel;
        }
    }
    public List<String> findDbFileName(){
        List<String> list = new ArrayList<>(10);
        try {
            Files.walk(Paths.get(AppSettingsState.getInstance().localSavePosition), 1).forEach(path -> {
                Path name = path.getName(path.getNameCount() - 1);
                String dbName = name.toString();
                if(dbName.endsWith(".mv.db")){
                    String cleanName = dbName.substring(0, dbName.lastIndexOf('.', dbName.length() - 6));
                    list.add(cleanName);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

package red.medusa.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.extern.slf4j.Slf4j;
import red.medusa.github.SegmentGithubService;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.service.service.SegmentEntityService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/07 周一
 */
@Slf4j
public abstract class SegmentSwitchableBranchDialog implements SegmentComponent {
    private String branchName = "";
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
            if (!this.branchName.isEmpty() && !appSettingsState.branchName.equals(this.branchName))
                appSettingsState.branchName = this.branchName;
            if (!this.dbName.isEmpty() && !appSettingsState.dbName.equals(this.dbName))
                appSettingsState.dbName = this.dbName;
            SegmentGithubService.getInstance().changeForSettings();
            isNeedSwitch = false;
        }
    }

    private class SwitchBranchDialog extends DialogWrapper {
        private final AppSettingsState appSettingsState = AppSettingsState.getInstance();

        public SwitchBranchDialog() {
            super(true); // use current window as parent
            init();
            setTitle("切换分支/DB");
            setResizable(true);
        }

        @Override
        protected JComponent createCenterPanel() {
            Box dialogPanel = Box.createVerticalBox();
            ComboBox<String> comboBox = new ComboBox<>();
            ComboBox<String> dbNameComboBox = new ComboBox<>();
            comboBox.setPreferredSize(new Dimension(300,30));
            dbNameComboBox.setPreferredSize(new Dimension(300,30));
            Box hbox01 = Box.createHorizontalBox();
            hbox01.add(new JLabel("分支名: "));
            hbox01.add(comboBox);
            Box hbox02 = Box.createHorizontalBox();
            hbox02.add(new JLabel("文件名: "));
            hbox02.add(dbNameComboBox);
            dialogPanel.add(hbox01);
            dialogPanel.add(hbox02);

            try {
                SegmentGithubService service = SegmentGithubService.getInstance();
                Set<String> branchNames = service.findLocalBranchNames();
                List<String> dbNames = service.findDbFileName();
                int i = 0;
                for (String name : branchNames) {
                    comboBox.addItem(name);
                    if (appSettingsState.branchName.equals(name))
                        comboBox.setSelectedIndex(i);
                    i++;
                }
                for (int j = 0; j < dbNames.size(); j++) {
                    dbNameComboBox.addItem(dbNames.get(j));
                    if (appSettingsState.dbName.equals(dbNames.get(j)))
                        dbNameComboBox.setSelectedIndex(j);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        branchName = (String) e.getItem();
                        isNeedSwitch = true;
                    }
                }
            });
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

}

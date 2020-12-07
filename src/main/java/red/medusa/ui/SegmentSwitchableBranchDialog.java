package red.medusa.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.extern.slf4j.Slf4j;
import red.medusa.github.SegmentGithubService;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.service.service.SegmentEntityService;

import javax.swing.*;
import java.awt.event.*;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/07 周一
 */
@Slf4j
public abstract class SegmentSwitchableBranchDialog implements SegmentComponent {
    private String branchName = "";
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
                if(entityService.isClose()){
                    NotifyUtils.notifyInfo("数据库已断开连接...");
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        getJComponent().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SegmentEntityService entityService = SegmentEntityService.getInstance();
                entityService.recreateEntityManagerFactory();
                if(entityService.isOpen()){
                    NotifyUtils.notifyInfo("数据库已建立连接...");
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void doSwitchBranch() {
        if (!branchName.equals("")) {
            AppSettingsState appSettingsState = AppSettingsState.getInstance();
            appSettingsState.branchName = branchName;
            SegmentGithubService.getInstance().changeForSettings();
            isNeedSwitch = false;
        }
    }

    private class SwitchBranchDialog extends DialogWrapper {
        private final AppSettingsState appSettingsState = AppSettingsState.getInstance();

        public SwitchBranchDialog() {
            super(true); // use current window as parent
            init();
            setTitle("切换分支");
            setResizable(true);
        }

        @Override
        protected JComponent createCenterPanel() {
            JPanel dialogPanel = new JPanel();
            ComboBox<String> comboBox = new ComboBox<>();
            try {
                Set<String> branchNames = SegmentGithubService.getInstance().findLocalBranchNames();
                int i = 0;
                for (String name : branchNames) {
                    comboBox.addItem(name);
                    if (appSettingsState.branchName.equals(name)) {
                        comboBox.setSelectedIndex(i);
                    }
                    i++;
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
            dialogPanel.add(comboBox);
            return dialogPanel;
        }
    }

}

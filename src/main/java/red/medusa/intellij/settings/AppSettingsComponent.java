package red.medusa.intellij.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import red.medusa.github.SegmentGithubService;
import red.medusa.ui.NotifyUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
@Slf4j
public class AppSettingsComponent {
    private final JPanel myMainPanel;
    // github 地址
    private final JBTextField githubUrlText = new JBTextField(50);
    private final JButton testGithubUrlBtn = new JButton("Test connection");
    private final JBLabel testGithubUrlResult = new JBLabel("Github url: ");
    // db 文件名
    private final JBTextField dbName = new JBTextField();
    // 远程分支
    private final ComboBox<String> branchNames = new ComboBox<>(300);
    // 本地存放位置 - 默认 user.home
    private final TextFieldWithBrowseButton localSavePosition = new TextFieldWithBrowseButton();
    // 是否自动提交
    private final JBCheckBox autoCommitStatus = new JBCheckBox("Auto commit?");
    // 是否拉取
    private final JBCheckBox useGithubStatus = new JBCheckBox("Use github?");
    // 是否自动push
    private final JBCheckBox autoPushStatus = new JBCheckBox("Auto push?");

    public AppSettingsComponent() {
        log.info("construct AppSettingsComponent");
        @NotNull Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        Project project = ProjectManager.getInstance().getDefaultProject();
        if (openProjects.length > 0) {
            project = openProjects[0];
        }

        localSavePosition.addBrowseFolderListener("文件存放位置", "", project, new FileChooserDescriptor(
                false,
                true,
                false,
                false,
                false,
                false));

        log.info(AppSettingsState.getInstance().toString());

        JPanel checkBox = new JPanel();
        branchNames.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        checkBox.add(autoCommitStatus);
        checkBox.add(useGithubStatus);
        checkBox.add(autoPushStatus);

        try {
            Set<String> allBranch = SegmentGithubService.getInstance().findLocalBranchNames();
            int i = 0;
            String currentBranchName = AppSettingsState.getInstance().branchName;
            for (String branch : allBranch) {
                branchNames.addItem(branch);
                if(currentBranchName.equals(branch)){
                    branchNames.setSelectedIndex(i);
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel testUrlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        testUrlPanel.add(githubUrlText);
        testUrlPanel.add(testGithubUrlBtn);

        initControls();

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Github url: "), testUrlPanel, 1, false)
                .addComponent(testGithubUrlResult, 1)
                .addLabeledComponent(new JBLabel("Branch name: "), branchNames, 1, false)
                .addLabeledComponent(new JBLabel("DB name: "), dbName, 1, false)
                .addLabeledComponent(new JBLabel("Local save dir: "), localSavePosition, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .addComponent(checkBox, 1)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return githubUrlText;
    }

    /*
        view
     */
    public boolean getAutoCommitStatus() {
        return autoCommitStatus.isSelected();
    }

    public boolean getAutoPushStatus() {
        return autoPushStatus.isSelected();
    }

    public void setAutoCommitStatus(boolean newStatus) {
        autoCommitStatus.setSelected(newStatus);
    }

    public void setGithubUrlText(@NotNull String newText) {
        githubUrlText.setText(newText);
    }

    public void setDbNameText(@NotNull String newText) {
        dbName.setText(newText);
    }

    public void setLocalSavePosition(@NotNull String newText) {
        localSavePosition.setText(newText);
    }

    public void setUseGithubStatus(boolean newStatus) {
        useGithubStatus.setSelected(newStatus);
    }

    public void setAutoPushStatus(boolean newStatus) {
        autoPushStatus.setSelected(newStatus);
    }

    @NotNull
    public String getGithubUrlText() {
        return githubUrlText.getText();
    }

    @NotNull
    public String getDbNameText() {
        return dbName.getText();
    }

    @NotNull
    public String getBranchName() {
        String selectedItem = (String) branchNames.getSelectedItem();
        if (selectedItem == null)
            return "";
        return selectedItem;
    }

    @NotNull
    public String getLocalSavePosition() {
        return localSavePosition.getText();
    }

    public boolean getUseGithubStatus() {
        return useGithubStatus.isSelected();
    }

    /*
        Event
     */
    private void initControls() {
        testGithubUrlResult.setVisible(false);
        testGithubUrlBtn.addActionListener(e -> {
            String text = githubUrlText.getText();
            if (text != null && text.trim().length() > 0) {
                TestGithubUrlResult testResult = SegmentGithubService.getInstance().testRemoteUrlAvailability(text);

                if (testResult.isUrlAvailability) {
                    this.testGithubUrlResult.setText("<html><font color=green>Successful</span></html>");
                    this.branchNames.removeAllItems();
                    Set<String> branchNames = testResult.getBranchNames();
                    int i = 0;
                    boolean find = false;
                    AppSettingsState settingsState = AppSettingsState.getInstance();
                    for (String branchName : branchNames) {
                        this.branchNames.addItem(branchName);
                        // 看看之前是否存在
                        if (settingsState.branchName.trim().length() > 0 &&
                                settingsState.branchName.trim().equals(branchName)) {
                            this.branchNames.setSelectedIndex(i);
                            find = true;
                        }
                        i++;
                    }
                    // 默认使用第一项
                    if (!(branchNames.isEmpty() || find)) {
                        this.branchNames.setSelectedIndex(0);
                    }
                } else {
                    this.testGithubUrlResult.setText("<html><font color=red>Failure</span></html>");
                }
                this.testGithubUrlResult.setVisible(true);
            }
        });
    }

    public void resetBranchNames() {
        try {
            SegmentGithubService instance = SegmentGithubService.getInstance();

            if (instance.hasLocalRepo()) {
                Set<String> allBranch = instance.findLocalBranchNames();
                String currentBranch = instance.getCurrentBranch();
                branchNames.removeAllItems();
                if (allBranch.isEmpty()) {
                    branchNames.addItem(AppSettingsState.DEFAULT_BRANCH_NAME);
                } else {
                    int num = 0;
                    for (String branch : allBranch) {
                        branchNames.addItem(branch);
                        if (branch.equals(currentBranch)) {
                            branchNames.setSelectedIndex(num);
                        }
                        num++;
                    }
                }
            } else {
                branchNames.removeAllItems();
                branchNames.addItem(AppSettingsState.DEFAULT_BRANCH_NAME);
            }

        } catch (Exception e) {
            NotifyUtils.notifyError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TestGithubUrlResult {
        private boolean isUrlAvailability;
        private Set<String> branchNames;
    }
}











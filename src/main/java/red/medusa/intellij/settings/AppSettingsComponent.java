package red.medusa.intellij.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
@Slf4j
public class AppSettingsComponent {
    private final JPanel myMainPanel;
    // db 文件名
    private final JBTextField dbName = new JBTextField();
    // 本地存放位置 - 默认 user.home
    private final TextFieldWithBrowseButton localSavePosition = new TextFieldWithBrowseButton();
    // 是否允许多连接
    private final JBCheckBox permitMultipleConnection = new JBCheckBox("Permit multiple connection?");


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

        JPanel checkBox = new JPanel();
        checkBox.add(permitMultipleConnection);

        myMainPanel = FormBuilder.createFormBuilder()
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
        return dbName;
    }

    /*
        view
     */
    public void setDbNameText(@NotNull String newText) {
        dbName.setText(newText);
    }
    public void setLocalSavePosition(@NotNull String newText) {
        localSavePosition.setText(newText);
    }
    public void setPermitMultipleConnection(boolean newStatus) {
        permitMultipleConnection.setSelected(newStatus);
    }

    public String getDbNameText() {
        return dbName.getText();
    }
    public String getLocalSavePosition() {
        return localSavePosition.getText();
    }
    public boolean getPermitMultipleConnection() {
        return permitMultipleConnection.isSelected();
    }
}











package red.medusa.intellij.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import red.medusa.github.SegmentGithubService;
import red.medusa.service.service.SegmentEntityService;

import javax.swing.*;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Segment DB Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getGithubUrlText().equals(settings.githubUrl);
        modified |= !mySettingsComponent.getDbNameText().equals(settings.dbName);
        modified |= !mySettingsComponent.getBranchName().equals(settings.branchName);
        modified |= !mySettingsComponent.getLocalSavePosition().equals(settings.localSavePosition);
        modified |= !mySettingsComponent.getAutoCommitStatus() == settings.autoCommit;
        modified |= !mySettingsComponent.getAutoPushStatus() == settings.autoPush;
        modified |= !mySettingsComponent.getUseGithubStatus() == settings.useGithub;
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.githubUrl = mySettingsComponent.getGithubUrlText();
        settings.dbName = mySettingsComponent.getDbNameText();
        settings.localSavePosition = mySettingsComponent.getLocalSavePosition();
        settings.useGithub = mySettingsComponent.getUseGithubStatus();
        settings.autoCommit = mySettingsComponent.getAutoCommitStatus();
        settings.autoPush = mySettingsComponent.getAutoPushStatus();
        settings.branchName = mySettingsComponent.getBranchName();

        /*
            配置仓库
         */
        SegmentGithubService.getInstance().changeForSettings();

        /*
            从新切换仓库地址
         */
        SegmentEntityService.getInstance().recreateEntityManagerFactory();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setGithubUrlText(settings.githubUrl);
        mySettingsComponent.setDbNameText(settings.dbName);
        mySettingsComponent.setLocalSavePosition(settings.localSavePosition);
        mySettingsComponent.setUseGithubStatus(settings.useGithub);
        mySettingsComponent.setAutoCommitStatus(settings.autoCommit);
        mySettingsComponent.setAutoPushStatus(settings.autoPush);
        mySettingsComponent.resetBranchNames();
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }


}

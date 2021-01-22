package red.medusa.intellij.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;
import red.medusa.service.service.SegmentEntityService;

import javax.swing.*;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

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
        boolean modified = !mySettingsComponent.getDbNameText().equals(settings.dbName);
        modified |= !mySettingsComponent.getLocalSavePosition().equals(settings.localSavePosition);
        modified |= !mySettingsComponent.getPermitMultipleConnection() == settings.permitMultipleConnection;
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.dbName = mySettingsComponent.getDbNameText();
        settings.localSavePosition = mySettingsComponent.getLocalSavePosition();
        settings.permitMultipleConnection = mySettingsComponent.getPermitMultipleConnection();
        /*
            配置仓库
         */
        SegmentEntityService.getInstance().recreateEntityManagerFactory();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setDbNameText(settings.dbName);
        mySettingsComponent.setLocalSavePosition(settings.localSavePosition);
        mySettingsComponent.setPermitMultipleConnection(settings.permitMultipleConnection);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }


}

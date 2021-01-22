package red.medusa.intellij.settings;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
@State(
        name = "org.intellij.sdk.settings.AppSettingsState",
        storages = {@Storage("SegmentSettingsPlugin.xml")}
)
@Slf4j
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    // 数据库名字
    public volatile String dbName = "Segment";
    // 保存的文件位置
    public String localSavePosition = System.getProperty("user.home")+"/Segment";
    // 是否能多连接
    public volatile boolean permitMultipleConnection = true;


    public static AppSettingsState getInstance() {
        return ServiceManager.getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean isInitAvailable() {
        return localSavePosition.trim().length() > 0 && dbName.trim().length() > 0;
    }

}

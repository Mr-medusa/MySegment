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
    //----------------------------------------- 本地仓库配置 -------------------------------------------
    // 默认分支名
    public static final String DEFAULT_BRANCH_NAME = "refs/heads/master";
    // 数据库名字
    public volatile String dbName = "Segment";
    public volatile String branchName = DEFAULT_BRANCH_NAME;
    // 保存的文件位置
    public String localSavePosition = System.getProperty("user.home")+"/Segment";
    // 是否自动提交
    public volatile boolean autoCommit = false;
    //--------------------------------------  远程仓库配置 ----------------------------------------------
    // github 地址
    public volatile String githubUrl = "https://github.com";
    // 是否使用Github作为远程仓库
    public volatile boolean useGithub = false;
    public volatile boolean autoPush = false;


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

    public boolean isCloneAvailable() {
        return githubUrl.trim().length() > 0 &&
                branchName.trim().length() > 0 &&
                localSavePosition.trim().length() > 0 &&
                dbName.trim().length() > 0;
    }

    @Override
    public String toString() {
        return "AppSettingsState{" +
                "githubUrl='" + githubUrl + '\'' +
                ", dbName='" + dbName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", localSavePosition='" + localSavePosition + '\'' +
                ", autoCommit=" + autoCommit +
                ", useGithub=" + useGithub +
                ", autoPush=" + autoPush +
                '}';
    }
}

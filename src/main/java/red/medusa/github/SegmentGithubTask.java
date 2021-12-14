package red.medusa.github;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.ui.NotifyUtils;

/**
 * @author huguanghui
 * @since 2020/11/28 周六
 */
@Slf4j
public class SegmentGithubTask {

    private final SegmentGithubService githubService = SegmentGithubService.getInstance();
    private final AppSettingsState appSettingsState = AppSettingsState.getInstance();

    public SegmentGithubTask() {
    }

    public void projectOpened(Project project) {
        new Task.Backgroundable(project, "Load segment", false) {
            @SneakyThrows
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                indicator.setText("Check segment exits");
                /*
                    先初始化一个
                 */
                if (!githubService.hasLocalRepo()) {
                    if (!appSettingsState.useGithub && appSettingsState.isInitAvailable()) {
                        indicator.setText("Init repo");
                        githubService.initRepo();
                    }
                }
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                log.info("onThrowable");
                NotifyUtils.notifyWarning(error.getMessage());
                error.printStackTrace();
            }
        }.queue();
    }

    public void projectClosed() {
        try {
            if(!SegmentAppUtils.closeWhenOnlyOneProject()){
                return;
            }
            boolean mayBeNeedPush = false;
            boolean updateFlag = SegmentGithubContext.updateFlag();
            if (appSettingsState.autoCommit && updateFlag){
                log.info("commit...");
                mayBeNeedPush = githubService.commitFile();
            }
            if (appSettingsState.useGithub && appSettingsState.autoPush && mayBeNeedPush) {
                log.info("push...");
                githubService.push();
            }
            githubService.clear();
        } catch (Exception e) {
            log.info("commit or push failure...");
            e.printStackTrace();
        }
    }
}














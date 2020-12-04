package red.medusa.intellij.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import red.medusa.github.SegmentGithubTask;
import red.medusa.service.service.SegmentEntityService;

@Slf4j
public class ProjectOpenCloseListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        // [AWT-EventQueue-0]
        new SegmentGithubTask().projectOpened(project);
    }

    @Override
    public void projectClosed(@NotNull Project project) {

        SegmentEntityService.getInstance().finishService();

        new SegmentGithubTask().projectClosed();
    }

}


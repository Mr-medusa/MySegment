package red.medusa.intellij.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * @author huguanghui
 * @since 2020/11/30 周一
 */
public class SegmentAppUtils {
    public static Project getProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        return projects.length > 0 ? projects[0] : ProjectManager.getInstance().getDefaultProject();
    }

    public static ToolWindow toolWindow() {
        return ToolWindowManager.getInstance(getProject()).getToolWindow("Segment");
    }

}

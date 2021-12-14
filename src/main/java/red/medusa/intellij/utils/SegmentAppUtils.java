package red.medusa.intellij.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;

/**
 * @author huguanghui
 * @since 2020/11/30 周一
 */
public class SegmentAppUtils {

    public static Project getProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        return openProjects.length == 0 ? ProjectManager.getInstance().getDefaultProject() : openProjects[0];
    }

    /**
     * 在idea 2020.2.3版本使用某些便携机该方法会一直阻塞
     *
     * @return
     * @date 2021-12-14
     */
    // public static Project getProject() {
    //     Promise<DataContext> focusAsync = DataManager.getInstance().getDataContextFromFocusAsync();
    //     try {
    //         DataContext dataContext = focusAsync.blockingGet(Integer.MAX_VALUE);
    //         if (dataContext != null) {
    //             return dataContext.getData(PlatformDataKeys.PROJECT);
    //         }
    //
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //
    //     return ProjectManager.getInstance().getDefaultProject();
    // }
    public static ToolWindow toolWindow() {
        return ToolWindowManager.getInstance(getProject()).getToolWindow("Segment");
    }

    public static boolean closeWhenOnlyOneProject() {
        WindowManager windowManager = WindowManager.getInstance();
        IdeFrame[] allProjectFrames = windowManager.getAllProjectFrames();
        return allProjectFrames.length == 1;
    }
}

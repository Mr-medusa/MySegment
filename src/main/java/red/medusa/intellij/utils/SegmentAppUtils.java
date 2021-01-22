package red.medusa.intellij.utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.concurrency.Promise;

/**
 * @author huguanghui
 * @since 2020/11/30 周一
 */
public class SegmentAppUtils {
    public static Project getProject() {
        Promise<DataContext> focusAsync = DataManager.getInstance().getDataContextFromFocusAsync();
        try {
            DataContext dataContext = focusAsync.blockingGet(Integer.MAX_VALUE);
            if (dataContext != null){
                return dataContext.getData(PlatformDataKeys.PROJECT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ProjectManager.getInstance().getDefaultProject();
    }

    public static ToolWindow toolWindow() {
        return ToolWindowManager.getInstance(getProject()).getToolWindow("Segment");
    }

}

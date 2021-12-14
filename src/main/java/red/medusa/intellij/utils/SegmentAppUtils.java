package red.medusa.intellij.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;

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

    public static boolean closeWhenOnlyOneProject(){
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        try {
            FileWriter fileWriter = new FileWriter("F:\\idea-project.txt");
            for (Project project : projects) {
                fileWriter.write(project.getBasePath()+","+project.getProjectFilePath()+","+project.getName()+"\n\n");
            }
            fileWriter.close();
            return projects.length == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

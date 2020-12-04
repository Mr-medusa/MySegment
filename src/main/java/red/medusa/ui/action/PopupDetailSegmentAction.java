package red.medusa.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.medusa.intellij.SdkIcons;
import red.medusa.intellij.utils.SegmentAppUtils;

import javax.swing.*;

public class PopupDetailSegmentAction extends AnAction {

    public PopupDetailSegmentAction() {
        this(" - 查看 - ", "", SdkIcons.Sdk_detailView_icon);
    }

    public PopupDetailSegmentAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ToolWindow toolWindow = SegmentAppUtils.toolWindow();
        if (toolWindow != null) {
            Content content = toolWindow.getContentManager().getContent(2);
            toolWindow.getContentManager().setSelectedContent(content,true,true);
        }
    }
}




















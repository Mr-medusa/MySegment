package red.medusa.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.medusa.intellij.SdkIcons;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.Segment;
import red.medusa.ui.context.SegmentContextHolder;

import javax.swing.*;

public class PopupAddSegmentAction extends AnAction {

    public PopupAddSegmentAction() {
        this(" - 添加 - ", "", SdkIcons.Sdk_add_icon);
    }

    public PopupAddSegmentAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ToolWindow toolWindow = SegmentAppUtils.toolWindow();
        if (toolWindow != null) {
            SegmentContextHolder.setSegment(new Segment());
            toolWindow.getContentManager().selectNextContent();
            Content selectedContent = toolWindow.getContentManager().getSelectedContent();
            toolWindow.getContentManager().requestFocus(selectedContent,true);
        }
    }
}




















package red.medusa.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.medusa.intellij.SdkIcons;
import red.medusa.intellij.utils.SegmentAppUtils;

import javax.swing.*;

@Slf4j
public class PopupEditSegmentAction extends AnAction {

    public PopupEditSegmentAction() {
        this(" - 修改 - ", "", SdkIcons.Sdk_editSource_icon);
    }

    public PopupEditSegmentAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ToolWindow toolWindow = SegmentAppUtils.toolWindow();
        if (toolWindow != null) {
            toolWindow.getContentManager().selectNextContent();
            Content selectedContent = toolWindow.getContentManager().getSelectedContent();
            toolWindow.getContentManager().requestFocus(selectedContent,true);
        }
    }
}




















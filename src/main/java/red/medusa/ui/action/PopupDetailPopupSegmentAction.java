package red.medusa.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.medusa.intellij.SdkIcons;
import red.medusa.ui.SegmentDetailDialog;

import javax.swing.*;

public class PopupDetailPopupSegmentAction extends AnAction {

    public PopupDetailPopupSegmentAction() {
        this(" - Popup - ", "", SdkIcons.Sdk_popup_icon);
    }

    public PopupDetailPopupSegmentAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new SegmentDetailDialog(null).show();
    }
}




















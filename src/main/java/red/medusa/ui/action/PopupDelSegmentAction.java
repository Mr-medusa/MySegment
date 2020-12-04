package red.medusa.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.medusa.intellij.SdkIcons;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.ui.controls.SegmentDelInquiryDialogWrapper;
import red.medusa.ui.segment_action.SegmentAction;
import red.medusa.ui.table.SegmentTableModel;

import javax.swing.*;

public class PopupDelSegmentAction extends AnAction {
    private final SegmentTableModel segmentTableModel;
    private final int row;
    public PopupDelSegmentAction(SegmentTableModel segmentTableModel,int row) {
        this(" - 删除 - ", "", SdkIcons.Sdk_deleteHovered_icon,segmentTableModel,row);
    }

    public PopupDelSegmentAction(
            @Nullable String text,
            @Nullable String description,
            @Nullable Icon icon,
            SegmentTableModel segmentTableModel,int row) {
        super(text, description, icon);
        this.segmentTableModel = segmentTableModel;
        this.row = row;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ToolWindow toolWindow = SegmentAppUtils.toolWindow();
        if (toolWindow != null) {
            SegmentAction segmentAction = new SegmentAction();
            segmentAction.find();
            if(new SegmentDelInquiryDialogWrapper().showAndGet()){
                segmentAction.deleteSegment();
                this.segmentTableModel.removeRow(this.row);
                this.segmentTableModel.fireTableDataChanged();
            }
        }
    }
}




















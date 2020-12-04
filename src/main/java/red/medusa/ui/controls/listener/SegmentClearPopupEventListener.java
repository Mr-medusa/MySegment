package red.medusa.ui.controls.listener;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;
import red.medusa.intellij.SdkIcons;
import red.medusa.ui.SegmentAddOrEdit;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class SegmentClearPopupEventListener extends MouseAdapter {
    private final SegmentAddOrEdit segmentAddOrEdit;

    public SegmentClearPopupEventListener(SegmentAddOrEdit segmentAddOrEdit) {
        this.segmentAddOrEdit = segmentAddOrEdit;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 3) {
            Point point = e.getPoint();
            final DefaultActionGroup group = new DefaultActionGroup();
            group.add(new AnAction("- Clear -","", SdkIcons.Sdk_clear_icon) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    segmentAddOrEdit.clear();
                }
            });
            group.add(new AnAction("- Refresh -","", SdkIcons.Sdk_refresh_icon) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    segmentAddOrEdit.refresh();
                }
            });
            final ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(ActionPlaces.TOOLWINDOW_POPUP, group);
            popupMenu.getComponent().show(e.getComponent(), point.x, point.y);
        }
    }
}

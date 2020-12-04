package red.medusa.ui.controls;

import com.intellij.openapi.ui.DialogWrapper;
import red.medusa.service.entity.Segment;
import red.medusa.ui.context.SegmentContextHolder;

import javax.swing.*;
import java.awt.*;

public class SegmentDelInquiryDialogWrapper extends DialogWrapper {
    private final Segment segment = SegmentContextHolder.getSegment();
    public SegmentDelInquiryDialogWrapper() {
        super(true); // use current window as parent
        init();
        setTitle("刪除" + segment.getName());
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html><font color=red>你确定要删除" + segment.getName() + "吗?</font></html>");
//        label.setPreferredSize(new Dimension(100, 50));
        dialogPanel.add(label, BorderLayout.CENTER);
        return dialogPanel;
    }
}
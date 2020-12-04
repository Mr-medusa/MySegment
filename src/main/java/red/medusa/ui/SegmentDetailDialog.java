package red.medusa.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Url;
import red.medusa.ui.controls.SegmentEditorTextField;
import red.medusa.ui.segment_action.SegmentAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

public class SegmentDetailDialog extends DialogWrapper {
    private static int count = 0;
    private final Segment segment = new SegmentAction().find();

    public SegmentDetailDialog() {
        super(true); // use current window as parent
        init();
        setTitle(segment.getName());
        // 可以打开多个模态框
        setModal(false);
        setResizable(true);
        setSize(600, 425);
        Point location = getLocation();
        int offsetRate = count++;
        int offsetSize = offsetRate * 30;
        setLocation(location.x + offsetSize, location.y + offsetSize);
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setPreferredSize(new Dimension(600, 425));
        String langContent = segment.getLangDep() != null ? segment.getLangContent().name() : "java";
        SegmentEditorTextField textField = new SegmentEditorTextField(
                segment.getContent() != null ? segment.getContent() : "",
                SegmentAppUtils.getProject(),
                langContent,
                new Dimension(600, 425),
                true,
                EditorEx.VERTICAL_SCROLLBAR_RIGHT);

        dialogPanel.add(textField,BorderLayout.CENTER);
        // 连接
        Box verticalBox = Box.createVerticalBox();
        Set<Url> urls = segment.getUrls();
        int i = 0;
        for (Url url : urls) {
            String urlStr = url != null ? url.toString() : "www.baidu.com/index.html";
            JLabel jLabel = new JLabel("<html><a href='" + urlStr + "'>" + urlStr + "</a></html> ");
            jLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            jLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BrowserUtil.browse(urlStr);
                }
            });
            verticalBox.add(jLabel);
            if (i++ == 3)
                break;
        }
        dialogPanel.add(verticalBox,BorderLayout.SOUTH);
        return dialogPanel;
    }
}
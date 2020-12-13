package red.medusa.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.Content;
import red.medusa.service.entity.LangType;
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

    private final Segment segment;
    private final Content content;
    private SegmentEditorTextField textField;

    public SegmentDetailDialog(Content content) {
        super(true);
        this.segment = new SegmentAction().find();
        this.content = content;
        init();
        setTitle(segment.getName());
        // 可以打开多个模态框
        setModal(false);
        setResizable(true);
        scrollToTop(textField);
    }

    @Override
    protected JComponent createCenterPanel() {
        Box verticalBox = Box.createVerticalBox();
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(getSegmentEditor());
        horizontalBox.add(getPopupList());

        verticalBox.add(horizontalBox);

        Box urlHBox = Box.createHorizontalBox();
        urlHBox.add(getUrls());
        urlHBox.add(Box.createHorizontalGlue());
        verticalBox.add(urlHBox);

        return verticalBox;
    }


    public JComponent getUrls() {
        // 连接
        Box verticalBox = Box.createVerticalBox();
        Set<Url> urls = segment.getUrls();
        int i = 0;
        for (Url url : urls) {
            String urlStr = url != null ? url.toString() : "www.baidu.com/index.html";
            String title = urlStr.length() > 100 ? urlStr.substring(0, 100) + "..." : urlStr;
            JLabel jLabel = new JLabel("<html><a href='" + urlStr + "'>" + title + "</a></html> ");
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

        return verticalBox;
    }

    public JComponent getSegmentEditor() {
        Set<Content> contents = segment.getContents();
        LangType langType = LangType.JAVA;
        String contentStr = "";
        if (!contents.isEmpty() && content == null) {
            Content content = contents.stream().findFirst().get();
            langType = content.getLangType();
            contentStr = content.getContent();
        } else if (!contents.isEmpty()) {
            for (Content content : contents) {
                if (content.getId()!=null && content.getId().equals(this.content.getId())) {
                    langType = content.getLangType();
                    contentStr = content.getContent();
                    break;
                }
            }
        }

        textField = new SegmentEditorTextField(
                contentStr,
                SegmentAppUtils.getProject(),
                langType.value,
                new Dimension(600, 425),
                true,
                EditorEx.VERTICAL_SCROLLBAR_RIGHT);

        scrollToTop(textField);

        return textField;
    }

    public JComponent getPopupList() {
        Box verticalBox = Box.createVerticalBox();
        for (Content content : segment.getContents()) {
            String title = content.getTitle() != null && !content.getTitle().trim().isEmpty() ?
                    content.getTitle() : LangType.JAVA.name();
            JButton button = new JButton(title) {
                final int weight = 85;
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(weight, 40);
                }
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(weight, 40);
                }
            };
            button.setToolTipText(content.getTitle());
            button.addActionListener(e -> {
                LangType langType = content.getLangType();
                String contentStr = content.getContent();
                textField.setFileType(SegmentEditorTextField.findFileType(langType.value));
                textField.getDocument().setReadOnly(false);
                textField.setText(contentStr);
                textField.getDocument().setReadOnly(true);
                scrollToTop(textField);
            });
            verticalBox.add(button);
        }
        verticalBox.add(Box.createVerticalGlue());
        return verticalBox;
    }

    public static void scrollToTop(SegmentEditorTextField segmentEditorTextField){
        segmentEditorTextField.scrollRectToVisible(new Rectangle(0, 0));
        segmentEditorTextField.revalidate();
    }
}

















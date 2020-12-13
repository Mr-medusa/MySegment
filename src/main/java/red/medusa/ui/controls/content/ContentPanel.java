package red.medusa.ui.controls.content;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.Content;
import red.medusa.service.entity.LangType;
import red.medusa.service.entity.Segment;
import red.medusa.ui.NotifyUtils;
import red.medusa.ui.SegmentDetailDialog;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.controls.SegmentEditorTextField;
import red.medusa.ui.controls.SegmentTextField;
import red.medusa.ui.segment_action.ContentAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/12 周六
 */
@Slf4j
public class ContentPanel extends Box {
    private final Content content;
    private final String DEL_COMMAND = "删除";

    private final JButton delBtn = new JButton(DEL_COMMAND);
    private final ComboBox<LangType> comboBox = new ComboBox<>();

    private final SegmentTextField textField = new SegmentTextField() {
        @Override
        public Insets getInsets() {
            return JBUI.insets(3);
        }
    };

    private final ContentPanelList contentPanelList;
    private SegmentEditorTextField segmentEditorTextField;
    public int editorHeight;

    public ContentPanel(Content content, ContentPanelList contentPanelList, int height) {
        super(BoxLayout.Y_AXIS);
        this.contentPanelList = contentPanelList;
        this.content = content;
        this.editorHeight = height;
        initWidgetProps();
        addContent();
        initImmutableEvent();
    }

    public ContentPanel(Content content, ContentPanelList contentPanelList) {
        this(content, contentPanelList, 190);
    }

    /*
        Event
     */
    public void initImmutableEvent() {
        this.delBtn.addActionListener(e -> {
            // view
            contentPanelList.contentBox.remove(this);
            contentPanelList.revalidate();

            // database
            if (content.getId() != null) {
                Segment segment = SegmentContextHolder.getSegment();
                Set<Content> contents = segment.getContents();
                Iterator<Content> iterator = contents.iterator();
                Content removeContent = null;
                while (iterator.hasNext()) {
                    Content next = iterator.next();
                    if (next.getId().equals(this.content.getId())) {
                        iterator.remove();
                        removeContent = next;
                        break;
                    }
                }
                if (removeContent != null)
                    new ContentAction().delete(removeContent);
            } else {
                NotifyUtils.notifyWarning("Content 还未保存成功,请稍后重试!");
            }
        });
        this.segmentEditorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String text = segmentEditorTextField.getText();
                content.setLangType((LangType) comboBox.getSelectedItem());
                content.setContent(text);
                contentPanelList.work(content);
            }
        });
        this.textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(javax.swing.event.DocumentEvent e) {
                String text = textField.getText();
                content.setLangType((LangType) comboBox.getSelectedItem());
                content.setTitle(text.equals(tip) ? "" : text);
                contentPanelList.work(content);
            }
        });
        this.comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    LangType langType = (LangType) comboBox.getSelectedItem();
                    segmentEditorTextField.setFileType(SegmentEditorTextField.findFileType(langType.value));

                    content.setLangType(langType);
                    contentPanelList.work(content);
                }
            }
        });
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      Widgets            +++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void addContent() {
        Box hBox = Box.createHorizontalBox();
        hBox.add(comboBox);
        hBox.add(textField);
        hBox.add(delBtn);
        this.add(hBox);
        this.segmentEditorTextField = new SegmentEditorTextField(
                content.getContent(),
                SegmentAppUtils.getProject(),
                content.getLangType().value,
                this.editorHeight,
                false);

        this.add(this.segmentEditorTextField);
        this.add(new JSeparator());

        SegmentDetailDialog.scrollToTop(this.segmentEditorTextField);
    }

    private final String tip = "请输入标题.....";

    private void initWidgetProps() {
        initCombobox();


        Color color = textField.getForeground();
        String title = this.content.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            textField.setText(title);
        } else {
            textField.setForeground(JBColor.GRAY);
            textField.setText(tip);
        }
        textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(tip)) {
                    textField.setText("");
                    textField.setForeground(color);
                }
            }

            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(tip);
                    textField.setForeground(JBColor.GRAY);
                }
            }
        });

        this.delBtn.setVisible(this.content.getId() != null);
    }

    private void initCombobox() {
        int i=0;
        for (LangType langType : LangType.values()) {
            comboBox.addItem(langType);
            if (content != null && content.getLangType() == langType) {
                comboBox.setSelectedIndex(i);
            }
            i++;
        }
    }
}



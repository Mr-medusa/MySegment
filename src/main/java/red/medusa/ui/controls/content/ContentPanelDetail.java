package red.medusa.ui.controls.content;

import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.SdkIcons;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.Content;
import red.medusa.ui.SegmentDetailDialog;
import red.medusa.ui.controls.SegmentEditorTextField;
import red.medusa.ui.controls.SegmentLabel;

import javax.swing.*;
import java.awt.*;

/**
 * @author huguanghui
 * @since 2020/12/12 周六
 */
@Slf4j
public class ContentPanelDetail extends Box {

    public ContentPanelDetail(Content content, Integer height) {
        super(BoxLayout.Y_AXIS);
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        SegmentLabel textField = new SegmentLabel("标题") {
            @Override
            public int getVerticalAlignment() {
                return SwingConstants.CENTER;
            }
        };
        jPanel.add(textField);
        SegmentLabel textField2 = new SegmentLabel("", 300) {
            @Override
            public int getVerticalAlignment() {
                return SwingConstants.CENTER;
            }
        };
        textField2.setText(content.getTitle());
        textField2.setToolTipText(content.getTitle());
        jPanel.add(textField2);
        javax.swing.JButton JButton = new JButton(" - Popup -", SdkIcons.Sdk_popup_icon);
        jPanel.add(JButton);

        this.add(jPanel);

        height = height == null ? 280 : height;
        SegmentEditorTextField segmentEditorTextField = new SegmentEditorTextField(
                content.getContent(),
                SegmentAppUtils.getProject(),
                content.getLangType().value,
                height,
                false);

        this.add(segmentEditorTextField);
        this.add(new JSeparator());

        SegmentDetailDialog.scrollToTop(segmentEditorTextField);
        /*
            Event
         */
        JButton.addActionListener(e -> new SegmentDetailDialog(content).show());
    }
}



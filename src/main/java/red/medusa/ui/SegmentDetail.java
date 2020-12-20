package red.medusa.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.service.entity.Img;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Url;
import red.medusa.ui.controls.SegmentLabel;
import red.medusa.ui.controls.SegmentLabel2;
import red.medusa.ui.controls.content.ContentPanelListDetail;
import red.medusa.ui.segment_action.SegmentAction;
import red.medusa.utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author huguanghui
 * @since 2020/11/25 周三
 */
@Slf4j
public class SegmentDetail implements SegmentComponent {

    private final Box body = Box.createVerticalBox();

    /*
        model
     */
    private final SegmentLabel2 nameLabel = new SegmentLabel2();
    private final JTextArea descLabel = new JTextArea();

    private final SegmentLabel2 createTimeLabel = new SegmentLabel2();
    private final SegmentLabel2 updateTimeLabel = new SegmentLabel2();

    private final SegmentLabel2 moduleLabel = new SegmentLabel2();
    private final SegmentLabel2 categoryLabel = new SegmentLabel2();


    private final Box urlBoxForAdd = Box.createVerticalBox();
    private final Box imgBoxForAdd = Box.createVerticalBox();

    /*
        layout
     */
    // 名字
    private final Box nameBox = Box.createHorizontalBox();
    // 模块版本
    private final Box moduleVersionBox = Box.createHorizontalBox();
    // 描述
    private final Box descBox = Box.createHorizontalBox();
    // 链接 链接
    private final Box urlBox = Box.createHorizontalBox();
    private final Box imgBox = Box.createHorizontalBox();
    // 内容
    private final ContentPanelListDetail contentPanelListDetail = new ContentPanelListDetail();
    // 时间
    private final Box timeBox = Box.createHorizontalBox();

    public SegmentDetail() {
        initControlsProp();
        initFrame();
        fillFrameContent();
        initControlsEvent();
    }

    private void initControlsEvent() {

    }

    private void initFrame() {
        int strut = 10;
        body.add(new JSeparator());
        body.add(nameBox);
        body.add(descBox);
        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(urlBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(imgBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(moduleVersionBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(timeBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(contentPanelListDetail);

        body.add(Box.createVerticalGlue());
    }

    private void fillFrameContent() {
        nameBox.add(new SegmentLabel("名字", 60, 30));
        nameBox.add(nameLabel);

        descBox.add(new SegmentLabel("描述"));
        descBox.add(descLabel);

        /*
            URL 列表
         */
        JPanel urlListPanel = new JPanel(new GridLayout(1, 1));
        urlListPanel.add(urlBoxForAdd);
        /*
            Img 列表
         */
        JPanel imgListPanel = new JPanel(new GridLayout(1, 1));
        imgListPanel.add(imgBoxForAdd);

        this.urlBox.add(new SegmentLabel("链接"));
        this.urlBox.add(urlListPanel);

        this.imgBox.add(new SegmentLabel("图片"));
        this.imgBox.add(imgListPanel);


        this.moduleVersionBox.add(new SegmentLabel("模块"));
        this.moduleVersionBox.add(moduleLabel);
        this.moduleVersionBox.add(new SegmentLabel("版本"));
        this.moduleVersionBox.add(categoryLabel);

        timeBox.add(new SegmentLabel("创建时间", 80));
        timeBox.add(createTimeLabel);
        timeBox.add(new SegmentLabel("修改时间", 80));
        timeBox.add(updateTimeLabel);

    }

    @Override
    public void refresh() {
        Segment segment = new SegmentAction().find();
        nameLabel.setText(segment.getName() != null ? segment.getName() : "");
        descLabel.setText(segment.getDescription() != null ? segment.getDescription() : "");
        createTimeLabel.setText(DateUtils.dateTime(segment.getCreateTime()));
        updateTimeLabel.setText(DateUtils.dateTime(segment.getUpdateTime()));
        moduleLabel.setText(segment.getModule() != null ? segment.getModule().toString() : "");
        categoryLabel.setText(segment.getCategory() != null ? segment.getCategory().toString() : "N/A");

        urlBoxForAdd.removeAll();
        if (!(segment.getUrls() == null || segment.getUrls().isEmpty())) {
            for (Url url : segment.getUrls()) {
                String urlStr = url != null ? url.toString() : "www.baidu.com/index.html";
                JLabel jLabel = new JLabel("<html><a href='" + urlStr + "'>" + urlStr + "</a></html>");
                jLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                jLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        BrowserUtil.browse(urlStr);
                    }
                });
                urlBoxForAdd.add(jLabel);
            }
        }
        imgBoxForAdd.removeAll();
        if (!(segment.getImgs() == null || segment.getImgs().isEmpty())) {
            int last = segment.getImgs().size() - 1;
            int i = 0;
            for (Img img : segment.getImgs()) {
                JBLabel jbLabel = new JBLabel();
                jbLabel.setIcon(new ImageIcon(img.getImage()));
                imgBoxForAdd.add(jbLabel);
                if (i++ != last)
                    imgBoxForAdd.add(Box.createVerticalStrut(10));
            }
        }

        this.contentPanelListDetail.refresh();
    }


    private void initControlsProp() {
        descLabel.setEditable(false);


    }

    @Override
    public JComponent getJComponent() {
        return new JBScrollPane(body);
    }
}

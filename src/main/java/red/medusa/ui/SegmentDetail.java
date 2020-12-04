package red.medusa.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.Img;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Url;
import red.medusa.ui.controls.SegmentEditorTextField;
import red.medusa.ui.controls.SegmentLabel;
import red.medusa.ui.controls.SegmentLabel2;
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

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      Editor            ++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private EditorTextField dependenceTextArea = new SegmentEditorTextField("", SegmentAppUtils.getProject(), null, new Dimension(0, 150), true);
    private EditorTextField contentTextArea = new SegmentEditorTextField("", SegmentAppUtils.getProject(), null, new Dimension(0, 300), true);

    private final SegmentLabel2 createTimeLabel = new SegmentLabel2();
    private final SegmentLabel2 updateTimeLabel = new SegmentLabel2();

    private final SegmentLabel2 moduleLabel = new SegmentLabel2();
    private final SegmentLabel2 versionLabel = new SegmentLabel2();


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
    // 依赖
    private final Box dependenceBox = Box.createHorizontalBox();
    // 内容
    private final Box contentBox = Box.createHorizontalBox();
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
        body.add(Box.createVerticalStrut(strut));
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
        body.add(dependenceBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(2));
        body.add(contentBox);
    }

    private void fillFrameContent() {
        nameBox.add(new SegmentLabel("名字"));
        nameBox.add(nameLabel);

        descBox.add(new SegmentLabel("描述"));
        descBox.add(descLabel);

        dependenceBox.add(new SegmentLabel("依赖"));
        dependenceBox.add(dependenceTextArea);

        contentBox.add(new SegmentLabel("内容"));
        contentBox.add(contentTextArea);

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
        this.moduleVersionBox.add(versionLabel);

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
        versionLabel.setText(segment.getVersion() != null ? segment.getVersion().toString() : "");

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
            for (Img img : segment.getImgs()) {
                JBLabel jbLabel = new JBLabel();
                jbLabel.setIcon(new ImageIcon(img.getImage()));
                imgBoxForAdd.add(jbLabel);
                JLabel jLabel = new JLabel(" ");
                jLabel.setSize(1,3);
                imgBoxForAdd.add(jLabel);
            }
        }
         /*
            内容 和 依赖
         */

        dependenceBox.remove(dependenceTextArea);
        contentBox.remove(contentTextArea);

        String langDep = segment.getLangDep() != null ? segment.getLangDep().name() : "java";
        String langContent = segment.getLangDep() != null ? segment.getLangContent().name() : "java";
        dependenceTextArea = new SegmentEditorTextField(segment.getDependence() != null ? segment.getDependence() : "", SegmentAppUtils.getProject(), langDep, new Dimension(0, 150), true);
        contentTextArea = new SegmentEditorTextField(segment.getContent() != null ? segment.getContent() : "", SegmentAppUtils.getProject(), langContent, new Dimension(0, 300), true);

        dependenceBox.add(dependenceTextArea);
        contentBox.add(contentTextArea);

    }


    private void initControlsProp() {
        descLabel.setEditable(false);


    }

    @Override
    public JComponent getJComponent() {
        return new JBScrollPane(body);
    }
}

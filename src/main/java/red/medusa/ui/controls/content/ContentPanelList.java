package red.medusa.ui.controls.content;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Content;
import red.medusa.service.entity.Segment;
import red.medusa.service.service.SegmentEntityService;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.segment_action.ContentAction;
import red.medusa.ui.segment_action.SegmentAction;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/12 周六
 */
@Slf4j
public class ContentPanelList extends Box {
    private final JPanel  addBtnJPanel = new JPanel(new BorderLayout());
    private final SegmentEntityService segmentEntityService = SegmentEntityService.getInstance();
    protected Box contentBox = Box.createVerticalBox();
    public ContentPanelList() {
        super(BoxLayout.Y_AXIS);
        this.add(contentBox);
    }

    public void refresh() {
        this.clear();

        Segment segment = SegmentContextHolder.getSegment();
        Set<Content> contents = segment.getContents();
        if (contents.isEmpty()) {
            Content content = new Content();
            content.setIndex(0);
            contents.add(content);

            // 无论如何都添加一个
            if(segment.getId() != null)
                new SegmentAction().merge(segment);
        }
        boolean first = true;
        for (Content content : contents) {
            if (first) {
                addContentWidthHeight(content, 350);
                first = false;
            } else {
                this.addContent(content);
            }
        }
    }
    private void clear() {
        this.contentBox.removeAll();
        addAddBtn();
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      view            ++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void addContentWidthHeight(Content content, int height) {
        contentBox.add(new ContentPanel(content, this,height));
    }
    private void addContent(Content content) {
        contentBox.add(new ContentPanel(content, this));
    }
    private void addAddBtn() {
        String ADD_COMMAND = "添加";
        JButton addBtn = new JButton(ADD_COMMAND);
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        addBtn.addActionListener(e -> {
            Segment segment = SegmentContextHolder.getSegment();
            Content content = new Content();
            if (segment.getId() == null) {
                segment.getContents().add(content);
                content.setIndex(segment.getContents().size()-1);
                addContent(content);
            } else {
                addContent(new ContentAction().addOne());
            }
        });

        addBtnJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        addBtnJPanel.add(addBtn);
        this.add(addBtnJPanel);
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      database            ++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    protected void work(Content content) {
        segmentEntityService.doWork(content);
    }


}



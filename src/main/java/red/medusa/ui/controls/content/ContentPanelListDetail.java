package red.medusa.ui.controls.content;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Content;
import red.medusa.service.entity.Segment;
import red.medusa.ui.context.SegmentContextHolder;

import javax.swing.*;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/12 周六
 */
@Slf4j
public class ContentPanelListDetail extends Box {

    protected Box contentBox = Box.createVerticalBox();
    public ContentPanelListDetail() {
        super(BoxLayout.Y_AXIS);
        this.add(contentBox);
    }

    public void refresh() {
        this.contentBox.removeAll();

        Segment segment = SegmentContextHolder.getSegment();
        Set<Content> contents = segment.getContents();
        if (contents.isEmpty()) {
            Content content = new Content();
            content.setIndex(segment.getContents().size()-1);
            contents.add(content);
        }
        boolean first = true;
        for (Content content : contents) {
            if (first) {
                addContentWidthHeight(content,350);
                first = false;
            } else {
                this.addContent(content);
            }
        }
    }
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      view            ++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void addContentWidthHeight(Content content,int height) {
        contentBox.add(new ContentPanelDetail( content,height));
    }
    private void addContent(Content content) {
        contentBox.add(new ContentPanelDetail(content,null));
    }

}



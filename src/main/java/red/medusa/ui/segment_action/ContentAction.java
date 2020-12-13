package red.medusa.ui.segment_action;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Content;
import red.medusa.service.entity.Segment;
import red.medusa.service.service.SegmentEntityService;
import red.medusa.ui.context.SegmentContextHolder;

/**
 * @author huguanghui
 * @since 2020/11/26 周四
 */
@Slf4j
public class ContentAction {
    SegmentEntityService segmentEntityService = SegmentEntityService.getInstance();

    public void delete(Content content) {
        log.info("delete content in segment: {}", content);
        segmentEntityService.delete(content);
    }

    public Content addOne() {
        Segment segment = SegmentContextHolder.getSegment();
        Content content = new Content();
        segment.getContents().add(content);
        content.setIndex(segment.getContents().size()-1);

        Segment merge = segmentEntityService.merge(segment);
        SegmentContextHolder.setSegment(merge);
        log.info("merge segment for content: {}", content);

        return content;
    }
}













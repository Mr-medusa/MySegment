package red.medusa.service.service;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Content;
import red.medusa.service.entity.Segment;
import red.medusa.ui.context.SegmentContextHolder;

/**
 * @author huguanghui
 * @since 2020/11/29 周日
 */
@Slf4j
public class SegmentEntityService extends BaseEntityService {

    private final DebounceWorker debounceWorker = new DebounceWorker(3000);

    public SegmentEntityService() {
    }

    public void doWork() {
        Segment segment = SegmentContextHolder.getSegment();
        debounceWorker.run(() -> {
            if (segment.getName() == null || segment.getName().trim().isEmpty()) {
                // 注释掉烦人的提示
                // NotifyUtils.notifyWarning("名字不能为空!");
                return;
            }
            if (segment.getModule() == null || segment.getCategory() == null) {
//                NotifyUtils.notifyWarning("请先选择模块或版本!");
                return;
            }
            log.info("merge segment: {}", segment);
            Segment mergedSegment = this.merge(segment);
            SegmentContextHolder.setSegment(mergedSegment);
        });
    }

    public void doWork(Content content) {
        debounceWorker.run(() -> {
            if (content.getId() == null) {
                Segment segment = SegmentContextHolder.getSegment();
                int i = 0;
                for (Content segmentContent : segment.getContents()) {
                    if (content.getIndex() == i++ && segmentContent.getId() != null) {
                        content.setId(segmentContent.getId());
                    }
                }
                if (content.getId() == null)
                    return;
            }
            log.info("merge content: {}", content);
            this.merge(content);
        });
    }
    //------------------------------------------------------------------------------------

    /*
       -- singleton implement --
    */
    public static SegmentEntityService getInstance() {
        return Singleton.INSTANCE.getSegmentEntityService();
    }

    private enum Singleton {
        INSTANCE(new SegmentEntityService());
        private final SegmentEntityService segmentEntityService;

        Singleton(SegmentEntityService segmentEntityService) {
            this.segmentEntityService = segmentEntityService;

        }

        public SegmentEntityService getSegmentEntityService() {
            return segmentEntityService;
        }
    }


}

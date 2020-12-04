package red.medusa.service.service;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Segment;
import red.medusa.ui.NotifyUtils;
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
                return;
            }
            if (segment.getModule() == null || segment.getVersion() == null) {
                NotifyUtils.notifyWarning("请先选择模块或版本!");
                return;
            }

            Segment mergedSegment = this.merge(segment);
            SegmentContextHolder.setSegment(mergedSegment);

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

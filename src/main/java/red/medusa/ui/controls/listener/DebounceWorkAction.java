package red.medusa.ui.controls.listener;

import red.medusa.service.service.SegmentEntityService;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public interface DebounceWorkAction {
    SegmentEntityService segmentEntityService = SegmentEntityService.getInstance();

    default void work() {
        segmentEntityService.doWork();
    }

}

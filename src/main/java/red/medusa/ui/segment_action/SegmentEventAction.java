package red.medusa.ui.segment_action;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.BaseEntity;
import red.medusa.service.service.SegmentEntityService;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
@Slf4j
public abstract class SegmentEventAction<T extends BaseEntity> {

    protected static final SegmentEntityService segmentEntityService = SegmentEntityService.getInstance();

    public void persist(T t) {
        segmentEntityService.persist(t);
    }

    public void delete(T t) {
        segmentEntityService.delete(t);

    }

    public abstract Object list();
}




















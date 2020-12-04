package red.medusa.ui.segment_action;

import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Segment;
import red.medusa.ui.context.SegmentContextHolder;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

/**
 * @author huguanghui
 * @since 2020/11/26 周四
 */
@Slf4j
public class SegmentAction extends SegmentEventAction<Segment> {
    /*
        查询列表
     */
    @Override
    public List<Object[]> list() {
        Function<EntityManager, List<Object[]>> function = entityManager -> {
            String sql = "select s.name,s.description,m.name as mName,s.id from segment s left join module m on s.module_id=m.id order by m.id,s.version_id,s.id";
            List<Object[]> list = entityManager.createNativeQuery(sql).getResultList();
            entityManager.clear();
            return list;
        };
        return segmentEntityService.list(function);
    }

    /*
        根据列表项查询
     */
    public Segment find() {
        Segment segment = SegmentContextHolder.getSegment();
        if (segment.getId() == null)
            return segment;
        Segment segmentFuture = segmentEntityService.find(segment.getId(), Segment.class);
        SegmentContextHolder.setSegment(segmentFuture);
        return segmentFuture;
    }


    public void deleteSegment() {
        delete(SegmentContextHolder.getSegment());
        SegmentContextHolder.setSegment(new Segment());
    }
}

package red.medusa.ui.segment_action;

import red.medusa.service.service.DebounceWorker;
import red.medusa.service.service.SegmentEntityService;
import red.medusa.ui.controls.table.SegmentTable;
import red.medusa.ui.controls.table.SegmentTableModel;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author huguanghui
 * @since 2020/12/02 周三
 */
public class QueryAction {
    private final SegmentTableModel segmentTableModel;
    private static final SegmentEntityService segmentEntityService = SegmentEntityService.getInstance();
    private final DebounceWorker debounceWorker = new DebounceWorker(1000);

    public QueryAction(SegmentTable segmentTable) {
        this.segmentTableModel = segmentTable.getSegmentTableModel();
    }

    private final static String KEYWORD_SQL = "select s.name,m.name as mName,s.id from" +
            " segment s inner join module m on s.module_id=m.id" +
            " where s.name like ?1 or s.description like ?1" +
            " order by m.id,s.category_id,s.id";

    public void queryByKeyword(String keyword) {
        Function<EntityManager, List<Object[]>> function = entityManager -> {
            Query query = entityManager.createNativeQuery(KEYWORD_SQL);
            query.setParameter(1, "%" + keyword + "%");
            List<Object[]> resultList = query.getResultList();
            entityManager.clear();
            return resultList;
        };
        doQuery(function);
    }

    private final static String MODULE_SQL =
            "select s.name,m.name as mName,s.id from " +
                    "segment s inner join module m on s.module_id=m.id " +
                    "where m.id=?1 order by s.category_id ,s.id";

    public void queryByModule(Long moduleId) {
        if (moduleId == null)
            return;
        Function<EntityManager, List<Object[]>> function = entityManager -> {
            Query query = entityManager.createNativeQuery(MODULE_SQL);
            query.setParameter(1, moduleId);
            List<Object[]> resultList = query.getResultList();
            entityManager.clear();
            return resultList;
        };
        doQuery(function);
    }

    private final static String CATEGORY_SQL = "select s.name,m.name as mName,s.id from " +
            "segment s inner join module m on s.module_id=m.id where m.id=?1 and s.category_id=?2 order by s.id";

    public void queryByCategory(Long moduleId, Long categoryId) {
        if (moduleId == null || categoryId == null)
            return;
        Function<EntityManager, List<Object[]>> function = entityManager -> {
            Query query = entityManager.createNativeQuery(CATEGORY_SQL);
            query.setParameter(1, moduleId);
            query.setParameter(2, categoryId);
            List<Object[]> resultList = query.getResultList();
            entityManager.clear();
            return resultList;
        };
        doQuery(function);
    }


    private void doQuery(Function<EntityManager, List<Object[]>> function) {
        debounceWorker.run(() -> {
            List<Object[]> segments = segmentEntityService.list(function);
            if (segments.isEmpty()) {
                this.segmentTableModel.changeModule(new ArrayList<>());
            } else {
                this.segmentTableModel.changeModule(segments);
            }
        });
    }
}

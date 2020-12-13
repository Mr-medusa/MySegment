package red.medusa.ui.segment_action;

import red.medusa.service.entity.Category;

import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/26 周四
 */
public class CategoryAction extends SegmentEventAction<Category> {

    @Override
    public List<Category> list() {
        return segmentEntityService.list(Category.class);
    }
}

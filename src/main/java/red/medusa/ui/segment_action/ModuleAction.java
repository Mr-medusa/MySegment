package red.medusa.ui.segment_action;

import red.medusa.service.entity.Module;

import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/26 周四
 */
public class ModuleAction extends SegmentEventAction<Module> {

    @Override
    public List<Module> list() {
        return segmentEntityService.list(Module.class);
    }
}













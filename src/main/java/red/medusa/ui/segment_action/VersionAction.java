package red.medusa.ui.segment_action;

import red.medusa.service.entity.Version;

import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/26 周四
 */
public class VersionAction extends SegmentEventAction<Version> {

    @Override
    public List<Version> list() {
        return segmentEntityService.list(Version.class);
    }
}

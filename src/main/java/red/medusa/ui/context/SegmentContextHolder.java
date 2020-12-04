package red.medusa.ui.context;

import red.medusa.service.entity.Segment;

/**
 * @author huguanghui
 * @since 2020/11/29 周日
 */
public final class SegmentContextHolder {
    private static volatile Segment segment = new Segment();
    public static void setSegment(Segment segment){
        SegmentContextHolder.segment = segment;
    }
    public static Segment getSegment(){
       return segment;
    }
}

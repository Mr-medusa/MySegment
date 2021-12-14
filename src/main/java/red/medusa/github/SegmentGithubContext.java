package red.medusa.github;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author huguanghui
 * @date 2021/12/14
 */
public abstract class SegmentGithubContext {
    public static volatile AtomicBoolean updateFlag = new AtomicBoolean();

    public static void trueFlag() {
        updateFlag.set(true);
    }

    public static boolean updateFlag(){
        return updateFlag.getAndSet(false);
    }
}

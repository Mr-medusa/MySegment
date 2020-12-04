package red.medusa.service.service;

import java.util.Timer;
import java.util.TimerTask;


public class DebounceWorker {
    private Timer timer;
    private final long delay;

    public DebounceWorker(long delay) {
        this.delay = delay;
    }

    public void run(Runnable runnable) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer = null;
                runnable.run();
            }
        }, delay);
    }
}
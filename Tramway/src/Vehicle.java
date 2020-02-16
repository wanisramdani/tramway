import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class Vehicle extends Thread {
    /** Used to generate codes */
    static int count = 0;
    private int code;

    /** Current direction */
    TrafficDirection dir;

    /**
     * Used to wait for the animation to complete before progressing to the next segment.
     * `WorldController` should `canAdvance.release()` it once `worldView` finishes.
     */
    Semaphore canAdvance;

    BridgeArbiter bridgeArbiter;
    IntersectionArbiter intersectionArbiter;
    List[] segmentQueues;

    Vehicle(WorldModel worldMode) {
        code = count++;

        bridgeArbiter = worldMode.bridgeArbiter;
        intersectionArbiter = worldMode.intersectionArbiter;
        segmentQueues = worldMode.segmentQueues;

        // FIXME: Should it start with permits=1?
        canAdvance = new Semaphore(0);
    }

    int getCode() {
        return code;
    }

    abstract void advance();
    abstract void enter();
    abstract void leave();

    public void run() {
        try {
            while (true) {
                canAdvance.acquire();
                // TODO: To add "dynamism", delay restarting by some random millis
                // sleep((int)Math.random() * 1000);
                advance();
                // We were interrupted by World.stopAll() while we were busy
                if (isInterrupted()) throw new InterruptedException();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

}

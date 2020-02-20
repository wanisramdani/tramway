import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class Vehicle extends Thread {
    /** Used to generate codes */
    static int count = 0;
    private int code;

    // Used for view only
    boolean virgin;

    /** Logic segment */
    int segment = 0;

    /** Current direction */
    TrafficDirection dir;

    /**
     * Used to wait for the animation to complete before advancing to the next segment.
     * `WorldController` should `canAdvance.release()` it once `worldView` finishes.
     */
    Semaphore canAdvance;

    BridgeArbiter bridgeArbiter;
    IntersectionArbiter intersectionArbiter;
    List[] segmentQueues;

    Vehicle(WorldModel worldMode) {
        code = count++;
        virgin = true;

        bridgeArbiter = worldMode.bridgeArbiter;
        intersectionArbiter = worldMode.intersectionArbiter;
        segmentQueues = worldMode.segmentQueues;

        canAdvance = new Semaphore(1);
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
                // FIXME: Do this only if we're blocked by enter() for some time
                //delayAdvancing();
                advance();
                // We were interrupted by stopAll() while we were busy
                if (isInterrupted()) throw new InterruptedException();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    /**  To add "dynamism", delay restarting by some random millis */
    void delayAdvancing() throws InterruptedException {
       sleep((int)Math.random() * 1000);
    }

}
import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class Vehicle extends Thread {
    /** Current direction */
    TrafficDirection dir;

    /**
     * Used to wait for the animation to complete to progress to the next segment
     *  `WorldView` should `canAdvance.release()` it
     */
    Semaphore canAdvance;

    BridgeArbiter bridgeArbiter;
    IntersectionArbiter intersectionArbiter;
    List[] segmentQueues;

    Vehicle(WorldModel worldMode) {
        bridgeArbiter = worldMode.bridgeArbiter;
        intersectionArbiter = worldMode.intersectionArbiter;
        segmentQueues = worldMode.segmentQueues;

        canAdvance = new Semaphore(0);
    }

    abstract void advance();
    abstract void enter();
    abstract void leave();

    public void run() {
        try {
            while (true) {
                canAdvance.acquire();
                advance();
                // We were interrupted by World.stopAll() while we were busy
                if (isInterrupted()) throw new InterruptedException();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Named 'perhaps()' and not 'maybe()' to distinguish it from the Maybe monad.
     * @return `true` 50% of the time
     */
    static boolean perhaps() {
        return Math.random() < 50;
    }

}

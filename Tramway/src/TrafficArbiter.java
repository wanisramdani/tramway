import java.util.concurrent.Semaphore;

/**
 * Executes traffic synchronization algorithms on behalf of vehicles
 */
public abstract class TrafficArbiter {
    /**
     * Blocks the calling thread until permission to enter the next "logic segment" is given
     */
    abstract void getPermit(TrafficDirection currentDir);

    /**
     * Handles traffic management when a vehicle leaves a "logic segment"
     */
    abstract void releasePermit(TrafficDirection previousDir);

    /**
     * Alias for `sem.acquire()`. Used to keep Java methods similar to algorithms' "textbook syntax"
     */
    static void p(Semaphore sem) {
        sem.acquireUninterruptibly();
    }

    /**
     * Alias for `sem.release()`. Used to keep Java methods similar to algorithms' "textbook syntax"
     */
    static void v(Semaphore sem) {
        sem.release();
    }
}

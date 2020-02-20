package sample;

import java.util.concurrent.Semaphore;

public abstract class TrafficArbiter {
    void p(Semaphore sem) {
        sem.acquireUninterruptibly();
    }

    void v(Semaphore sem) {
        sem.release();
    }

    abstract void getPermit(TrafficDirection currentDir);

    abstract void releasePermit(TrafficDirection previousDir);

}

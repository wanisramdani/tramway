package sample;

import java.util.concurrent.Semaphore;

public class BridgeArbiter extends TrafficArbiter {

    TrafficDirection turn = TrafficDirection.WEST;

    int goingWest = 0;
    int goingEast = 0;
    Semaphore canGoWest =  new Semaphore(0, true);
    Semaphore canGoEast =  new Semaphore(0, true);
    Semaphore mutex = new Semaphore(1, true);

    /**
     * Performs enter() calls for the bridge crossing problem
     */
    @Override
    void getPermit(TrafficDirection dir) {
        switch (dir) {

            case WEST: /* TramGoingWest::enter() */ {
                p(mutex);
                goingWest++;
                if ( (turn == TrafficDirection.WEST && goingWest == 1)
                  || (turn == TrafficDirection.EAST && goingEast == 0) ) {
                    turn = TrafficDirection.WEST;
                    v(canGoWest);
                }
                v(mutex);

                p(canGoWest);
                return;
            }

            case EAST: /* TramGoingEast::enter() */ {
                p(mutex);
                goingEast++;
                if (  (turn == TrafficDirection.EAST && goingEast == 1)
                   || (turn == TrafficDirection.WEST && goingWest == 0) ) {
                    turn = TrafficDirection.EAST;
                    v(canGoEast);
                }
                v(mutex);

                p(canGoEast);
                return;
            }

        }
    }

    /**
     * Performs leave() calls for the bridge crossing problem
     */
    @Override
    void releasePermit(TrafficDirection dir) {
        switch (dir) {

            case EAST: /* TramGoingEast::leave() */ {
                p(mutex);
                goingEast--;
                if (goingEast > 0) {
                    v(canGoEast);
                } else {
                    if (goingWest > 0) {
                        turn = TrafficDirection.WEST;
                        v(canGoWest);
                    }
                }
                v(mutex);
                return;
            }

            case WEST: /* TramGoingWest::leave() */ {
                p(mutex);
                goingWest--;
                if (goingWest > 0) {
                    v(canGoWest);
                } else {
                    if (goingEast > 0) {
                        turn = TrafficDirection.EAST;
                        v(canGoEast);
                    }
                }
                v(mutex);
                return;
            }

        }
    }

}

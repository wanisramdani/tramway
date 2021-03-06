package sample;

import java.util.concurrent.Semaphore;

/**
 * Similar to La Circulation
 *  except you execute "Changement" when a tram arrives, not when the timer 'times out'
 */
public class IntersectionArbiter extends TrafficArbiter {

    // Only used for animation
    boolean isTramsTurn = true;

    int passingTrams = 0;

    Semaphore canGoNorth = new Semaphore(1, true);
    Semaphore lightNorthSem = new Semaphore(1, true);
    Semaphore canGoSouth = new Semaphore(1, true);
    Semaphore lightSouthSem = new Semaphore(1, true);
    Semaphore mutex = new Semaphore(1, true);

    @Override
    void getPermit(TrafficDirection dir) {
        System.out.println(Thread.currentThread().getName() + " called getPermit " + dir);

        switch (dir) {

            case NORTH: /* CarGoingNorth::enter() */ {
                p(canGoNorth);
                p(lightNorthSem);

                isTramsTurn = false;
                return;
            }

            case SOUTH: /* CarGoingSouth::enter() */ {
                p(canGoSouth);
                p(lightSouthSem);

                isTramsTurn = false;
                return;
            }

            case EAST:
            case WEST: /* Tram::enter() */ {
                p(mutex);
                passingTrams++;
                if (passingTrams == 1) {
                    v(mutex);
                    p(lightNorthSem);
                    p(lightSouthSem);
                    isTramsTurn = true;
                } else {
                    v(mutex);
                }

                return;
            }

        }
    }

    @Override
    void releasePermit(TrafficDirection dir) {
        System.out.println(Thread.currentThread().getName() + " called releasePermit " + dir);

        switch (dir) {

            case NORTH: /* CarGoingNorth::leave() */ {
                v(lightNorthSem);
                v(canGoNorth);

                return;
            }

            case SOUTH: /* CarGoingSouth::leave() */ {
                v(lightSouthSem);
                v(canGoSouth);

                return;
            }

            case EAST:
            case WEST: /* Tram::leave() */ {
                p(mutex);
                passingTrams--;
                if (passingTrams == 0) {
                    v(mutex);
                    v(lightNorthSem);
                    v(lightSouthSem);
                } else {
                    v(mutex);
                }

                return;
            }

        }
    }

}

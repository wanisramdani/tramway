import java.util.concurrent.Semaphore;

/**
 * Similar to La Circulation
 *  except you 'change lights' when a tram arrives, not when the "timer times out"
 */
public class IntersectionArbiter extends TrafficArbiter {

    int passingTrams = 0;
    /** Only used for animation */
    boolean isTramsTurn = false;

    Semaphore canGoNorth = new Semaphore(1, true), lightNorthSem = new Semaphore(1, true);
    Semaphore canGoSouth = new Semaphore(1, true), lightSouthSem = new Semaphore(1, true);
    Semaphore mutex = new Semaphore(1, true);

    @Override
    void getPermit(TrafficDirection dir) {
        switch (dir) {

            case NORTH: /* CarGoingNorth::enter() */ {
                p(canGoNorth);
                p(lightNorthSem);
            }

            case SOUTH: /* CarGoingSouth::enter() */ {
                p(canGoSouth);
                p(lightSouthSem);
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
                    isTramsTurn = false;
                } else {
                    v(mutex);
                }

                return;
            }

        }
    }

}

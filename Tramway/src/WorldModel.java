import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldModel {
    BridgeArbiter bridgeArbiter;
    IntersectionArbiter intersectionArbiter;

    /**
     * Used to calculate 'delta' of tram in each section (see the map)
     *
     * See:
     * - Collections.syncronizedList(...)
     * - [ArrayList](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)
     */
    List[] segmentQueues = {
        Collections.synchronizedList(new ArrayList<Tram>()),
        Collections.synchronizedList(new ArrayList<Tram>()),
        Collections.synchronizedList(new ArrayList<Tram>()),
        Collections.synchronizedList(new ArrayList<Tram>())
    };
    List<Car> carsGoingNorthQueue = Collections.synchronizedList(new ArrayList<>());
    List<Car> carsGoingSouthQueue = Collections.synchronizedList(new ArrayList<>());

    boolean stopped = false;

    WorldModel() {
        bridgeArbiter = new BridgeArbiter();
        intersectionArbiter = new IntersectionArbiter();
        // ...
    }

    /**
     * Named 'perhaps()' and not 'maybe()' to distinguish it from the Maybe monad.
     * @return `true` 50% of the time
     */
    static boolean perhaps() {
        return Math.random() < 0.50;
    }

    void perhapsCreateCar() {
        if (perhaps() && carsGoingNorthQueue.size() < 3) {
            Car x = new Car(this, TrafficDirection.NORTH);
            carsGoingNorthQueue.add(x);
            // worldWorld.addCar(x.getId(), TrafficDirection.NORTH);
            // x.start();
        }

        if (perhaps() && carsGoingSouthQueue.size() < 3) {
            Car x = new Car(this, TrafficDirection.SOUTH);
            carsGoingSouthQueue.add(x);
            // worldWorld.addCar(x.getId(), TrafficDirection.SOUTH);
            // x.start();
        }
    }

    void startAll() {
        // start randomCarGenerator thread
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (stopped) { this.cancel(); return; }
                        perhapsCreateCar();
                    }
                },
                0,
                3000 // 3 sec
        );
    }

    void stopAll() {
        for (List<Vehicle> queue : segmentQueues) {
            for (Vehicle v : queue) {
                v.interrupt();
            }
        }

        for (List<Vehicle> queue : new List[]{carsGoingNorthQueue, carsGoingSouthQueue}) {
            for (Vehicle v : queue) {
                v.interrupt();
            }
        }

        stopped = true;
    }

}

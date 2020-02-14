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
        Collections.synchronizedList(new ArrayList<Vehicle>()),
        Collections.synchronizedList(new ArrayList<Vehicle>()),
        Collections.synchronizedList(new ArrayList<Vehicle>()),
        Collections.synchronizedList(new ArrayList<Vehicle>())
    };

    // TODO: Maybe create carsGoingNorthQueue and carsGoingSouthQueue?

    WorldModel() {
        bridgeArbiter = new BridgeArbiter();
        intersectionArbiter = new IntersectionArbiter();
        // ...
    }

    void startAll() {
        // start randomCarGenerator thread maybe
    }

    void stopAll() {
        // for each vehicle in each section
        // vehicle.interrupt();
    }

}

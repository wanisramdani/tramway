public class Tram extends Vehicle {

    /** logic segment */
    int segment = 0;

    public Tram(WorldModel worldMode) {
        super(worldMode);
    }

    @Override
    void advance() {
        enter();

        // TODO: Ask WorldControl to update WorldView's "ViewModel"
        //  something like this: `world.announce(this, oldSection, newSection);`
        int oldSegment = segment;
        segment = (segment + 1) % 4; // newSegment
        segmentQueues[oldSegment].remove(0);
        segmentQueues[segment].add(this);

        leave();
    }

    @Override
    void enter() {
        /*
        segments
        0: bridge + dir = EAST
        1: intersection + dir = EAST
        2: intersection + dir = WEST
        3: bridge + dir = WEST
        */
        switch (segment) {
            case 0: bridgeArbiter.getPermit(TrafficDirection.EAST); break;
            case 1: intersectionArbiter.getPermit(TrafficDirection.EAST); break;
            case 2: intersectionArbiter.getPermit(TrafficDirection.WEST); break;
            case 3: bridgeArbiter.getPermit(TrafficDirection.WEST); break;
        }

    }

    @Override
    void leave() {

    }

}

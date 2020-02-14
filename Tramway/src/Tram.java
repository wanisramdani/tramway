public class Tram extends Vehicle {

    static int count = 0;
    int segment = 0;

    // Each tram has a code: 'A', 'B', ... 'Z'
    private char code;

    public Tram(WorldModel worldMode) {
        super(worldMode);
        code = (char) ('A' + count++);
    }

    char getCode() {
        return code;
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

    // TODO: How to calculate this?
    // TODO: Maybe move to WorldView?
    static double DURATION_PER_TRAM = 0.25;
    double calculateDelta() {
        int pos = segmentQueues[segment].indexOf(this) + 1;
        return pos * DURATION_PER_TRAM;
    }

}

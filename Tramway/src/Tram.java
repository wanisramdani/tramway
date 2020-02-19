public class Tram extends Vehicle {

    public Tram(WorldModel worldModel) {
        super(worldModel);
    }

    @Override
    void advance() {
        enter();
        leave();

        // inform the world!
        // TODO: Ask WorldControl or WorldModel to update their data
        //  something like this: `world.announce(this, oldSection, newSection);`
        int oldSegment = segment;
        segment = (segment + 1) % 4; // newSegment
        segmentQueues[oldSegment].remove(0);
        segmentQueues[segment].add(this);
    }

    @Override
    void enter() {
        switch (segment) {
            case 0: bridgeArbiter.getPermit(TrafficDirection.EAST); break;
            case 1: intersectionArbiter.getPermit(TrafficDirection.EAST); break;
            case 2: intersectionArbiter.getPermit(TrafficDirection.WEST); break;
            case 3: bridgeArbiter.getPermit(TrafficDirection.WEST); break;
        }
    }

    @Override
    void leave() {
        switch (segment) {
            case 0: bridgeArbiter.releasePermit(TrafficDirection.EAST); break;
            case 1: intersectionArbiter.releasePermit(TrafficDirection.EAST); break;
            case 2: intersectionArbiter.releasePermit(TrafficDirection.WEST); break;
            case 3: bridgeArbiter.releasePermit(TrafficDirection.WEST); break;
        }
    }

}

public class Car extends Vehicle {

    public Car(WorldModel worldMode) {
        super(worldMode);
        // TODO: Direction should be determined by WorldController
        dir = perhaps() ? TrafficDirection.NORTH : TrafficDirection.SOUTH;
    }

    @Override
    void advance() {
        enter();
        // maybeCrash();
        // ...
        leave();
    }

    @Override
    void enter() {
        intersectionArbiter.getPermit(dir);
    }

    @Override
    void leave() {
        intersectionArbiter.releasePermit(dir);
    }

    /*
    void maybeCrash() throws InterruptedException {
        // 'shouldCrash' is set by WorldController when user clicks 'Crash Car'
        // getShouldCrash() clears the shouldCrash state
        if (worldModel.getShouldCrash()) {
            throw new InterruptedException();
        }
    }
    */

}

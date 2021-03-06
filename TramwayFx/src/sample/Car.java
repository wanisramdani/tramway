package sample;

public class Car extends Vehicle {

    public Car(WorldModel worldModel, TrafficDirection dir) {
        // FIXME
        super(worldModel, new java.util.List[]{worldModel.carsGoingNorthQueue});
        this.dir = dir;
        this.setName("Car#" + getCode());
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

    // TODO: Implement maybeCrash()
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

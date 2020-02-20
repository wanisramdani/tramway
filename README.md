# Tramway
Concurrent Programming assignment

Process-based simulation in Java/JavaFX à la MVC (more like [MVP](https://stackoverflow.com/a/1317742) actually)

## Problem
A traffic lights management system...

## Approach
As can be seen in the screenshot below, a tram (starting from the bottom left side) pass by some "obstacles":
- The bridge
- the intersection
- the intersection again
- and finally, the bridge again

Passing by each of these "obstacles" requires executing a different algorithm. The tram needs to keep record of its current section to know what algorithm to execute.

`TrafficArbiter` classes (BridgeArbiter and IntersectionArbiter) are used to store shared state data and execute synchronization algorithms on behalf of vehicles (`Tram`s and `Car`s)

---

The path the tram takes can be split into four segments:
0. start to bridge
1. bridge to intersection
2. intersection to intersection
3. intersection to bridge

If it's in segment 0 it asks the bridge abriter for permission to go east,
...

## Algorithms

### Bridge crossing

**NOTE: Changement \*may\* happen when a Tram calls enter() or leave()**

```
turn = WEST

int goingWest = 0, goingEast = 0
Semaphore canGoWest = 0, canGoEast = 0
Semaphore mutex = 1
```

```
TramGoingWest::enter() {
  p(mutex);
  goingWest++;
  if ( (turn == WEST && goingWest == 1) || (turn == EAST && goingEast == 0) ) {
    turn = WEST;
    v(canGoWest);
  }
  v(mutex);

  p(canGoWest);
}

TramGoingWest::leave() {
  p(mutex);
  goingWest--;
  if (goingWest > 0) {
    v(canGoWest);
  } else {
    if (goingEast > 0) {
      turn = EAST;
      v(canGoEast);
    }
  }
  v(mutex);
}
```

```
TramGoingEast::enter() {
  p(mutex);
  goingEast++;
  if ( (turn == EAST && goingEast == 1) || (turn == WEST && goingWest == 0) ) {
    turn = EAST;
    v(canGoEast);
  }
  v(mutex);

  p(canGoEast);
}

TramGoingEast::leave() {
  p(mutex);
  goingEast--;
  if (goingEast > 0) {
    v(canGoEast);
  } else {
    if (goingWest > 0) {
      turn = WEST;
      v(canGoWest);
    }
  }
  v(mutex);
}
```

### Traffic intersection

**NOTE: "Changement" may happen when a Tram calls enter() or leave(), not timeout-based!**

```
int passingTrams = 0

Semaphore canGoNorth = 1, lightNorthSem = 1
Semaphore canGoSouth = 1, lightSouthSem = 1
Semaphore mutex = 1
```

```
CarGoingNorth::enter() {
  p(canGoNorth);
  p(lightNorthSem);
}

CarGoingNorth::leave() {
  v(lightNorthSem);
  v(canGoNorth);
}
```

```
CarGoingSouth::enter() {
  p(canGoSouth);
  p(lightSouthSem);
}

CarGoingSouth::leave() {
  v(lightSouthSem);
  v(canGoSouth);
}
```

```
Tram::enter() {
  p(mutex);
  passingTrams++;
  if (passingTrams == 1) {
    v(mutex);
    p(lightNorthSem);
    p(lightSouthSem);
  } else {
    v(mutex);
  }
}

Tram::leave() {
  p(mutex);
  passingTrams--;
  if (passingTrams == 0) {
    v(mutex);
    v(lightNorthSem);
    v(lightSouthSem);
  } else {
    v(mutex);
  }
}
```

## Implementation

![UML class diagrams](tramway-uml.png)

```java
class TramwaySimulation {

  WorldModel worldModel;
  WorldView worldModel;
  WorldController worldController;

  public TramwaySimulation() {
    worldModel = new WorldModel();
    worldController = new WorldController(worlModel);
    worldView = new WorldView(worldController);
  }

  void start() {
    worldView.startAll();
    worldModel.startAll();
    worldController.startAll();
  }

  void stop() {
    worldView.stopAll();
    worldModel.stopAll();
    worldController.startAll();
  }

}
```

### JavaFX Animation
WANIS: Why? What problems? How do it work?

- To actually animate `Tram`s and `Car`s on their paths, and to `play`, `pause`, and manually progress (`jumpTo`) animations: [PathTransition](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/PathTransition.html).

- To update relative/paused trams: [AnimationTimer](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html).

### Notes
- Tried to follow [Google's Java style guide](https://google.github.io/styleguide/javaguide.html)

- Use `Collections.syncronizedList(..)` with [ArrayList](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)

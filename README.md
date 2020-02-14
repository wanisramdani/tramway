# Tramway
Concurrent Programming assignment

![UML class diagrams](tramway-uml.png)

## Coding Style
Follow [Google's Java style guide](https://google.github.io/styleguide/javaguide.html)

Document your code: [How to Write Doc Comments for the Javadoc Tool](https://www.oracle.com/technetwork/articles/java/index-137868.html)


## JavaFX Animation
- To actually animate `Car`s and `Tram`s: [PathTransition](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/PathTransition.html).

- To `play`, `pause`, and manually progress (`jumpTo` or `playFrom`) animations: [Timeline](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/Timeline.html).

- To update relative/paused trams: [AnimationTimer](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html).

- MAYBE to be notified of Trams' animation progress: [KeyFrame](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/KeyFrame.html) or maybe [ObservableList](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html).


## Problems

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

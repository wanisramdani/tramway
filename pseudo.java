// TODO: Use JavaDoc
// TODO: Create diagrams from JavaDoc using UMLGraph

enum TrafficLightColor {
  RED,
  GREEN,
  YELLOW
}

enum TrafficDirection {
  NORTH,
  SOUTH,
  EAST,
  WEST
}

/**
 * View.
 *
 * @see https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html
 */
class WorldView extends AnimationTimer {

  /**
   * Used to calculate 'delta' of vehicles in each section (see the map)
   *
   * @see Collections.syncronizedList(...)
   * @see [ArrayList](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)
   */
  List[] sections = {
    Collections.syncronizedList(new ArrayList());
    Collections.syncronizedList(new ArrayList());
    Collections.syncronizedList(new ArrayList());
    Collections.syncronizedList(new ArrayList());
  }

  @Override
  public void handle(long now) {
    redraw();
  }

  public void redraw() {
    redrawLights();
    redrawTrams();
    redrawCars();
  }

  /**
   * Update the `color` attribute of all lights.
   *
   * @todo MAYBE instead of `onlyTrams`, Cars' traffic light color is green if sFeu.getPermits() > 0
   */
  void redrawLights() {
    /*
    TRAFFIC LIGHTS LIST:
    0: Before trams bridge WEST
    1: At trams bridge WEST
    2: At trams brige EAST
    3: After trams bridge EAST
    4: Cars SOUTH
    5: Cars NORTH
    6: Trams x Cars SOUTH
    7: Trams x Cars NORTH
    */

    L[0].color = L[1].color == RED ? YELLOW : GREEN;
    L[3].color = L[2].color == RED ? YELLOW : GREEN;
    L[4].color = L[5].color = onlyTrams ? GREEN : RED;
    L[6].color = L[7].color = onlyTrams ? RED : GREEN;

    // Meaning:
    // The actual changing variables are: L[1].color, L[2].color, and onlyTrams
  }

  void redrawTrams() {

    for (sectionQueue queue : sectionQueues) {
      Tram leadingTram = queue.get(0);
      List<Tram> otherTrams = queue.subList(1, queue.size());

      if (leadingTram.isPaused()) leadingTram.play();
      
      for (Tram tram : queue) {
        if (tram.getDuration() > tram.calculateDelta()) {
          tram.pause();
        }
        if (tram.getState() == State.PAUSED) {
          tram.jumptTo(Tram.calculateDelta())
        }
      }
    }

  }

  void redrawCars() {
    // ...
  }

}


/**
 * Model.
 */
class World {

  boolean stopped = false;

  void stop() {
    worldView.stop();
  }

}


class TramsController {

  TrafficDirection turn = EAST;

  int nTramsToEast = 0;
  int nTramsToWest = 0;
   
  /*
   * Implements bridge crossing problem solution
   * @todo complete it
   */
  void getPermit(TrafficDirection dir) {
    switch (dir) {
      case EAST:
        nTramsToEast++;
        break;

      case WEST:
        nTramsToWest++;
        break;
    }

    mutex.acquire();
    if (nTramsToEast > 0 && turn == EAST) {
      // ...
    }
    mutex.release();

    switch (dir) {
      case EAST:
        eastSemaphore.acquire();
        mutex.acquire();
        nTramsToEast--;
        mutex.release();
        break;

      case WEST:
        westSemaphore.acquire();
        mutex.acquire();
        nTramsToWest--;
        mutex.release();
        break;
    }

  }

}


class CarsController {

  boolean onlyTrams = false;

  int nTramsToWest = 0;
  int nTramsToEast = 0;
  int nCarsToNorth = 0;
  int nCarsToSouth = 0;

  /**
   * Completed on paper. Similar to La Circulation
   *  except you 'change lights' when a tram arrives, not when the "timer times out"
   */
  void getPermit(TrafficDirection dir) {
    switch (dir) {
      case EAST:
        nTramsToEast++;
        break;

      case WEST:
        nTramsToWest++;
        break;

      case NORTH:
        nCarsToNorth++;
        break;
      
      case SOUTH:
        nCarsToSouth++;
        break;
    }

    if (nTramsToEast > 0 || nTramsToWest > 0) {
      sema2.acquire();
      sema2.acquire();
      onlyTrams = true;
    }

  }

}


abstract class Vehicle extends Thread {
  /** Current direction */
  TrafficDirection dir;

  /**
   * Used to wait for the animation to complete to progress to the next segment
   *  `WorldView` should `canAdvance.release()` it */
  Semaphore canAdvance;

  public void run() {
    while (true) {
      canAdvance.acquire();
      advance();
    }
  }

  abstract void advance();
  abstract void enter();
  abstract void leave();

  /**
   * @note Named 'perhaps()' and not 'maybe()' to distinguish it from the Maybe<?> monad.
   * @return `true` 50% of the time
   */
  static boolean perhaps() {
    return Math.random() < 50;
  }

}


class Tram extends Vehicle {

  static int count = 0;
  int segment = 0;
  // Each tram has a code: 'A', 'B', ... 'Z'
  char code;

  public Tram() {
    code = 'A' + count++;
  }

  @Override
  void advance() {
    getPermit();
    // TODO: Ask WorldControl to update WorldView's "ViewModel"
    //  something like this: `world.announce(this, oldSection, newSection);`
    sectionQueues[section].add(code)
    section = (section + 1) % 4;
    sectionQueues[section].remove(0);
  }

  @Override
  void getPermit() {
    /*
    segments
    0: bridge + dir = EAST
    1: intersection + dir = EAST
    2: intersection + dir = WEST
    3: bridge + dir = WEST
    */
    switch (segment) {
      case 0: TramsController.getPermit(EAST); break;
      case 1: CarsController.getPermit(EAST); break;
      case 2: CarsController.getPermit(WEST); break;
      case 3: TramsController.getPermit(WEST); break;
    }
  }

  // TODO: How to calculate this?
  static double DURATION_PER_TRAM = 0.25;
  double calculateDelta() {
    int pos = sectionQueue[section].indexOf(code) + 1;
    return pos * DURATION_PER_TRAM;
  }

}


class Car extends Vehicle {

  TrafficDirection dir;

  Car() {
    dir = perhaps() ? TrafficDirection.NORTH : TrafficDirection.SOUTH;
  }

  @Override
  void advance() {
    getPermit();
  }

  @Override
  void getPermit() {
    CarsController.getPermit(dir);
  }

}

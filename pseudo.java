// Use JavaDoc and UMLGraph
/*
- Collections.syncronizedList(...)
- [ArrayList](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)
*/

enum TrafficLightColor {
  RED,
  GREEN,
  YELLOW
}

// rename to Traffic Direction?
enum Direction {
  NORTH,
  SOUTH,
  EAST,
  WEST
}

class World {

  // Used for calculating 'delta' of trams in each section (see the map)
  List[] sections = {
    Collections.syncronizedList(new ArrayList());    
    Collections.syncronizedList(new ArrayList());    
    Collections.syncronizedList(new ArrayList());    
    Collections.syncronizedList(new ArrayList());    
  }

  boolean stopped = false;

  void stop() {
    vizRW.acquireWrite();
    stopped = true;
    vizRW.releaseWrite();
  }

  void redraw() {
    vizRW.acquireRead();
    if (stopped) {
      vizRW.releaseRead();
      return;
    }

    redrawLights();
    redrawTrams();
    redrawCars(); // ?
  }

  /**
   * Update the `color` attribute of all lights.
   *
   * @note MAYBE instead of introducing a new variable (onlyTrams),  Cars' light color is green if sFeu.getPermits() > 0
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

}


class TramsController {

  Direction turn = EAST;

  int nTramsToEast = 0;
  int nTramsToWest = 0;
   
  /*
   * Implements bridge crossing problem solution
   * @todo complete it
   */
  void getPermit(Direction dir) {
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
  void getPermit(Direction dir) {
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


abstract class Vehicle {
  Direction dir;
  abstract void advance();
  abstract void enter();
  abstract void leave();
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
    segment = (segment + 1) % 4;

    /*
    sectionQueues[section].add(code)
    section = (section + 1) % 4;
    sectionQueues[section].remove(0);
    */
    // run animation...
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

  final static double DURATION_PER_TRAM = 0.25;
  double calculateDelta() {
    int pos = sectionQueue[section].indexOf(code) + 1;
    return pos * DURATION_PER_TRAM;
  }

}


class Car extends Vehicle {

  Direction dir;

  Car() {
    // TODO: randomly choose NORTH or SOUTH
    dir = NORTH;
  }

  @Override
  void advance() {
    getPermit();
    // run animation...
  }

  @Override
  void getPermit() {
    CarsChangement.getPermit(dir);
  }

}

// TODO: Create diagrams from JavaDoc using UMLGraph

class WorldSim {

  WorldModel worldModel;
  WorldView worldModel;
  boolean stopped;

  public WorldSim() {
    worldView = new WorldView();
    worldModel = new WorldModel();
  }

  void start() {
    stopped = false;
    worldView.startAll();
    worldModel.startAll();
  }

  void stop() {
    stopped = true;
    worldView.stopAll();
    worldModel.stopAll();
  }

}


// https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html
class WorldView extends AnimationTimer {

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

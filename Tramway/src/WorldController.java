import java.util.List;

public class WorldController implements WorldControllerInterface {

  WorldModel worldModel;
  WorldViewInterface worldView;

  WorldController(WorldModel worldModel) {
    this.worldModel = worldModel;
  }

  @Override
  public void updateView() {
    updateLights();
    updateTrams();
    updateCars();
  }

  @Override
  public void startAll() {
    // start viewAutoUpdater
    new java.util.Timer().schedule(
        new java.util.TimerTask() {
          @Override
          public void run() {
            updateView();
          }
        },
        0,
        250 // 0.25 sec
    );
  }

  @Override
  public void stopAll() {
    // cancel viewAutoUpdater
    // ...
  }

  void updateTrams() {
    Tram previousTram = null;

    for (List<Tram> segmentQueue: worldModel.segmentQueues) {
      for (int i = 0; i < segmentQueue.size(); i++) {
        boolean isFirstTram = i == 0;
        Tram tram = segmentQueue.get(i);
        int code = tram.getCode();

        // Handle new trams
        if (tram.virgin) {
          tram.virgin = false;
          worldView.createTram(code);
        }

        // Handle "dynamic" and relative animations
        if (isFirstTram) {
          worldView.setTramDynamic(code, true);
        } else {
          double followerProgress = worldView.getTramProgress(code);
          double leaderProgress = worldView.getTramProgress(previousTram.getCode());
          double minDelta = worldView.getDeltaConstant();
          double updatedProgress = calculateProgress(followerProgress, leaderProgress, minDelta);
          if (followerProgress != updatedProgress) {
            worldView.setTramDynamic(code, false);
            worldView.setTramProgress(code, updatedProgress);
          } else {
            worldView.setTramDynamic(code, true);
          }
        }
        previousTram = tram;

        // Handle out-of-sync'ness of the model and view
        int logicSegment = tram.segment;
        int graphicSegment = worldView.getGraphicSegment(code);
        if (graphicSegment > logicSegment) {
          worldView.setTramDynamic(code, false);
          worldView.setTramProgress(code, "segment_" + logicSegment + "_end");
          // It means the tram's animation in this segment has finished. Let the tram thread resume.
          tram.canAdvance.release();
        }

        // FIXME: Test for 100%
        if (worldView.getTramProgress(code) >= 130) {
          worldView.setTramProgress(code, "segment_0_start");
          // It means the tram's animation in this segment has finished. Let the tram thread resume.
          tram.canAdvance.release();
       }

      }
    }

  }

  double calculateProgress(double followerProgress, double leaderProgress, double minDelta) {
    double diff = leaderProgress - followerProgress;
    if (diff >= minDelta) {
      return followerProgress;
    } else {
      return leaderProgress - minDelta;
    }
  }

  void updateCars() {

    for (List<Car> segmentQueue: new List[]{worldModel.carsGoingNorthQueue, worldModel.carsGoingSouthQueue}) {
      for (int i = 0; i < segmentQueue.size(); i++) {
        Vehicle car = segmentQueue.get(i);
        int code = car.getCode();

        if (car.virgin) {
          car.virgin = false;
          worldView.createCar(code, car.dir);
        }

        // If both the animation and execution have finished, destroy it
        if (/*car.getState() == Thread.State.TERMINATED &&*/ worldView.getCarProgress(code) >= 10) {
          //segmentQueue.remove(car);
          worldView.destroyCar(code);
        }
      }
    }
    
    // ...
  }

  /**
   * Update the `color` attribute of all lights.
   * See map.txt
   */
  void updateLights() {
    // L
    TrafficColor G = TrafficColor.GREEN;
    TrafficColor R = TrafficColor.RED;
    TrafficColor Y = TrafficColor.YELLOW;

    if (worldModel.intersectionArbiter.isTramsTurn) {
      L(3, G); L(2, G); L(4, R);
      L(6, G); L(5, G); L(7, R);
    } else {
      L(3, R); L(2, Y); L(4, G);
      L(6, R); L(5, Y); L(7, G);
    }

    if (worldModel.bridgeArbiter.turn == TrafficDirection.EAST) {
      L(1, G); L(0, G);
      L(9, R); L(8, Y);
    } else { // turn = WEST
      L(1, R); L(0, Y);
      L(9, G); L(8, G);
    }

  }

  void L(int i, TrafficColor c) {
    worldView.setLightColor(i, c);
  }

}

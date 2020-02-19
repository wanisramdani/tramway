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
    // ...
  }

  @Override
  public void stopAll() {
    // ...
  }

  void updateTrams() {
    Tram previousTram = null;

    for (List<Tram> segmentQueue: worldModel.segmentQueues) {
      for (int i = 0; i < segmentQueue.size(); i++) {
        boolean isFirstTram = i == 0;
        Tram tram = segmentQueue.get(i);
        int code = tram.getCode();

        // Handle "dynamic" and relative animations
        if (isFirstTram) {
          worldView.setTramDynamic(tram.getCode(), true);
        } else {
          double followerProgress = worldView.getTramProgress(tram.getCode());
          double leaderProgress = worldView.getTramProgress(previousTram.getCode());
          double minDelta = worldView.getDeltaConstant();
          double updatedProgress = calculateProgress(followerProgress, leaderProgress, minDelta);
          if (followerProgress != updatedProgress) worldView.setTramDynamic(tram.getCode(), false);
          worldView.setTramProgress(tram.getCode(), updatedProgress);
        }
        previousTram = tram;

        // Handle out-of-sync'ness of the model and view
        int logicSegment = tram.segment;
        int graphicSegment = worldView.getGraphicSegment(code);
        if (logicSegment != graphicSegment) {
          worldView.setTramDynamic(code, false);
          worldView.setTramProgress(code, "segment_" + logicSegment + "_end");
          // It mean the tram's animation in this segment has finished. Let the tram thread resume.
          tram.canAdvance.release();
        }

      }
    }

  }

  double calculateProgress(double followerProgress, double leaderProgress, double minDelta) {
    double diff = leaderProgress - followerProgress;
    if (diff > minDelta) {
      return followerProgress;
    } else {
      return leaderProgress - minDelta;
    }
  }

  void updateCars() {
    // TODO
    // ...
  }

  /**
   * Update the `color` attribute of all lights.
   * See map.txt
   */
  void updateLights() {
    // TODO: Reduce the length of this method
    //   Whatever L = (int i, TrafficColor c) -> worldView.setLightColor(i, c);
    //   // 3 green -> 2 green -> 4 red
    //   L(3, G); L(2, G); L(4, R);

    TrafficColor G = TrafficColor.GREEN;
    TrafficColor R = TrafficColor.RED;
    TrafficColor Y = TrafficColor.YELLOW;

    if (worldModel.intersectionArbiter.isTramsTurn) {

      // 3 green -> 2 green -> 4 red
      worldView.setLightColor(3, G);
      worldView.setLightColor(2, G);
      worldView.setLightColor(4, R);

      // 6 green -> 5 green -> 7 red
      worldView.setLightColor(6, G);
      worldView.setLightColor(5, G);
      worldView.setLightColor(7, R);
    } else {
      // 3 red -> 2 yellow -> 4 green
      worldView.setLightColor(3, R);
      worldView.setLightColor(2, Y);
      worldView.setLightColor(4, G);

      // 6 red -> 5 yellow -> 7 green
      worldView.setLightColor(6, R);
      worldView.setLightColor(5, Y);
      worldView.setLightColor(7, G);
    }

    if (worldModel.bridgeArbiter.turn == TrafficDirection.WEST) {
      // 1 green -> 0 green
      worldView.setLightColor(1, G);
      worldView.setLightColor(0, G);

      // 9 red -> 8 yellow
      worldView.setLightColor(9, R);
      worldView.setLightColor(8, Y);
    } else { // turn = EAST
      // 1 red -> 0 yellow
      worldView.setLightColor(1, R);
      worldView.setLightColor(0, Y);

      // 9 green -> 8 green
      worldView.setLightColor(9, G);
      worldView.setLightColor(8, G);
    }

  }

}

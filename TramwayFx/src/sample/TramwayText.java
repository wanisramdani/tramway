package sample;

public class TramwayText {

  public static void main(String[] args) {
    TramwayText simulation = new TramwayText();
    simulation.start();
  }

  WorldModel worldModel;
  WorldViewInterface worldView;
  WorldController worldController;

  public TramwayText() {
    worldModel = new WorldModel();
    worldView = new WorldViewText();
    worldController = new WorldController(worldModel);
    worldController.worldView = worldView;
  }

  void start() {
    worldView.startAll();
    worldModel.startAll();
    worldController.startAll();
    
    worldController.startViewAutoUpdater();
  }

  void stop() {
    worldView.stopAll();
    worldModel.stopAll();
    worldController.startAll();
  }

}

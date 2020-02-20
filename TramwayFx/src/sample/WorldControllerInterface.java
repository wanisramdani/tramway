package sample;

public interface WorldControllerInterface {
    /**
     * Update worldView to match worldModel.
     * Maybe worldView should call it after each frame (animationTimer)
     */
    void updateView();

    void startAll();
    void stopAll();

    // TODO: maybe add `setMVC(WorldModelInterface, WorldViewInterface, WorldControllerInterface)` to all parties
    //      then use it like: worldController.setMVC(worldModel, worldView, null);
    //      and also: worldView.setMVC(null, null, WorldController);
}

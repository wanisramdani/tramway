package sample;

public interface WorldControllerInterface {
    /**
     * Update worldView to match worldModel.
     *
     * Either WorldView (in an AnimationTimer) call this each frame or WorldController updates 
     */
    void updateView();

    // TODO: Create WorldInterface that contrains setMVC, startAll, stopAll

    /**
     * Since different parts of our "MVC" may need other parties that aren't already created
     * and thus can't be passed to their constructor.
     *
     * Usage example:
     * worldView.setMVC(null, null, WorldController);
     * worldController.setMVC(worldModel, worldView, null);
     */
    // void setMVC(WorldModelInterface model, WorldViewInterface view, WorldControllerInterface controller);

    void startAll();

    void stopAll();

}

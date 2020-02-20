package sample;

public interface WorldViewInterface {

    void startAll();
    void stopAll();

    /**
     * Change the light color of a square or circular traffic light
     */
    void setLightColor(int lightId, TrafficColor color);

    double getDeltaConstant();

    // -------

    /**
     * Returns the segment code the tram is visually ("graphically") in.
     */
    int getGraphicSegment(int tramId);

    /**
     * Sets whether the animation should *progress* on its own ("dynamically")
     */
    void setTramDynamic(int tramId, boolean isDynamic);

    /**
     * Sets a specific animation progress point manually
     * Pattern: /^segment_(\d)_(start|end)$/
     *
     * EXAMPLES:
     * setTramProgress(0, "segment_2_end");
     * setTramProgress(0, "segment_3_start");
     */
    void setTramProgress(int tramId, String namedProgress);

    /**
     * Sets a specific animation progress point manually
     */
    void setTramProgress(int tramId, double dur);

    double getTramProgress(int tramId);

    /**
     * Create a new tram visual representation, optionally starting from the first section
     */
    void createTram(int tramId);

    /**
     * Destroys or simply hides (to be recycled) the tram visual representation
     */
    void destroyTram(int tramId);

    // -------

    /**
     * Create a new car visual representation, respecting the traffic direction
     * The animation should start playing automatically.
     *
     * e.g. For WorldViewFX, `createCar(13, TrafficDirection.SOUTH)`
     *      creates (or shows) a car that follows the carGoingSouth path
     */
    void createCar(int carId, TrafficDirection dir);

    /**
     * Destroys or simply hides (to be recycled) the car visual representation
     */
    void destroyCar(int carId);

    void setCarDynamic(int carId, boolean isDynamic);

    double getCarProgress(int carId);

    void setCarProgress(int carId, double dur);

    void setCarProgress(int carId, String namedProgress);

}

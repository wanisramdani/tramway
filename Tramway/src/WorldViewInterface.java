import java.time.Duration;

public interface WorldViewInterface {

    /**
     * Change the light color of a square or circular traffic light
     */
    void setLightColor(int lightId, TrafficColor color);

    // -------

    /**
     * Returns the segment code the tram is visually ("graphically") in.
     */
    int getGraphicSegment(int tramId);

    /*
    // Something like:
    int getGraphicSegment(int tramId) {
      Duration dur = getTramProgress(tramId);

      for (int i = 0; i < 4; i++) {
        Duration start = getCuePoint("segment_" + i + "_start");
        Duration end = getCuePoint("segment_" + i + "_end");
        if (dur >= start && dur <= end) {
          return i;
        }
      }

      throw new InvalidStateException("Uncovered interval: " + dur);
    }
    */

    /**
     * Sets whether the animation should *progress* on its own ("dynamically")
     */
    void setTramDynamic(int tramId, boolean isDynamic);

    /**
     * Sets a specific animation progress point manually
     * 
     * EXAMPLES:
     * setTramProgress(0, "segment_2_end");
     * setTramProgress(0, "segment_3_start");
     */
    void setTramProgress(int tramId, String namedDuration);

    /**
     * Sets a specific animation progress point manually
     */
    void setTramProgress(int tramId, Duration dur);

    Duration getTramProgress(int tramId);

    /**
     * Create a new tram visual representation, optionally starting from the first section
     */
    void createTram(int tramId);

    /**
     * Destroys or simply hides (to be recycled) the tram visual representation
     */
    void deleteTram(int tramId);

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
    void deleteCar(int carId);

    void setCarDynamic(int carId, boolean isDynamic);

    Duration getCarProgress(int carId);

    void setCarProgress(int carId, Duration dur);

}

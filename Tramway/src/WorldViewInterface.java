import java.time.Duration;

public interface WorldViewInterface {

    /**
     * Change the light color of a square or round traffic light
     */
    void setLightColor(int lightId, TrafficColor c);

    /**
     * Calls play() or pause() animation depending on isDynamic
     */
    void setTramDynamic(int tramId, boolean isDynamic);

    /**
     * jumpTo(dur)
     */
    void setTramProgress(int tramId, Duration dur);

    Duration getTramProgress(int tramId);

    /**
     * Get Point in Path
     */
    int getTramPoint(int tramId); // or maybe it returns Point

    // -------

    void setCarDynamic(int carId, boolean isDynamic);

    void setCarProgress(int carId, Duration dur);

    Duration getCarProgress(int carId);

    int getCarPoint(int carId);

}

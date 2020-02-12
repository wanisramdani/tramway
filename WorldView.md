# Tramway: WorldView
Or maybe `WorldPresentation`

See [`map.txt`](./map.txt)

## Legend
- Tramway lights are squares
- Car lights are round

## map.fxml
The map contains three paths:
- `TramPath`: The whole Tram journey, starting from `section0`
- `CarNorthPath`: Car going north
- `CarSouthPath`: Car going south

Each path should contain "point markers". \
For example, `CarNorthPath`: 
- `CarNorthPath.enteringPoint`
- `CarNorthPath.leavingPoint`

But more realistically, `TramPath`: 
- `section0StartPoint`
- `section1StartPoint`
- `section2StartPoint`
- `section3StartPoint`
- `bridgeStartPoint`
- `bridgeEndPoint`
- `intersection1StartPoint`
- `intersection2StartPoint`
- ...

Why? These are needed for determining the 'active section' (by calculating "currentPoint > nextPoint") to set progress (duration) programmatically. See bellow.

## WorldViewInterface

`class WorldView implements WorldViewInterface`

```java
interface WorldViewInterface {

  void setLightColor(int lightId, LightColor c);

  // -------

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
```

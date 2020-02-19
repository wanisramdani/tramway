import java.util.HashMap;
import java.util.Timer;

public class WorldViewText implements WorldViewInterface {

  public static void main(String[] args) {
    // FOR TESTING

    WorldViewText worldView = new WorldViewText();
    //worldView.fancy = true;
    worldView.startAll();

    // Test lights
    worldView.setLightColor(1, TrafficColor.GREEN);
    worldView.setLightColor(9, TrafficColor.RED);

    // Test trams
    worldView.createTram(0);
    worldView.setTramDynamic(0, true);

    worldView.createTram(1);
    worldView.setTramProgress(1, -40);

    // Test cars
    worldView.createCar(2, TrafficDirection.NORTH);
    worldView.createCar(3, TrafficDirection.SOUTH);
  }

  final static String VIEW_TEMPLATE =
          "                                     [9:R]..[8:Y] (7:G)....[6:R]...  \n" +
          "                                                       |  |       .  \n" +
          "                                                       |  |       .  \n" +
          "   +----------------+                +-----------------+--+--+    .  \n" +
          "  /                  \\              /                  |  |   \\ [5:Y]\n" +
          " /                    \\            /                   |  |   |      \n" +
          " |                     +----------+                    |  |   |      \n" +
          " \\                    /            \\                   |  |   |      \n" +
          "  \\                  /              \\                  |  |   /      \n" +
          "   +----------------+                +-----------------+--+--+       \n" +
          "                                                       |  |          \n" +
          "                                                       |  |          \n" +
          "         [0:G]...[1:G]                [2:Y].......[3:R]....(4:G)     \n" +
          "                                                                     " ;

  HashMap<Integer, Character> lightsMap = new HashMap<>();
  HashMap<Integer, Wrapper> vehiclesMap = new HashMap<>();
  Timer redrawer;
  boolean fancy = false;

  @Override
  public int getGraphicSegment(int id) {
    // meaning a vehicle V is in segment `SEGMENT_NUMBER` iif its `progress` is in `[START; END[`

    int progress = vehiclesMap.get(id).textVehicle.getProgress();

    for (int segmentNumber = 0; segmentNumber < TextPath.TRAM_PATH_SEGMENTS.length; segmentNumber++) {
      int[] segment = TextPath.TRAM_PATH_SEGMENTS[segmentNumber];
      if (progress >= segment[0] && progress < segment[1]) {
        return segmentNumber;
      }
    }

    // throw new IllegalStateException("Unmarked progress value: " + progress);
    return TextPath.TRAM_PATH_SEGMENTS.length - 1;
  }

  @Override
  public void createTram(int tramId) {
    TextVehicle textVehicle = new TextVehicle(
        (char) ('A' + tramId),
        TextPath.TRAM_START_POINT,
        TextPath.TRAM_PATH
    );
    addVehicle(tramId, textVehicle);
  }

  @Override
  public void destroyTram(int id) {
    vehiclesMap.get(id).ticker.cancel();
    vehiclesMap.remove(id);
  }

  void addVehicle(int id, TextVehicle v) {
    Wrapper wrapper = new Wrapper();

    wrapper.textVehicle = v;

    // Tick every half a second
    Timer ticker = new Timer();
    ticker.schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                if (wrapper.isPaused) {
                  return; // skip tick
                } else {
                  wrapper.textVehicle.setProgress(+1, true);
                }
              }
            },
            0,
            500
    );
    wrapper.ticker = ticker;

    vehiclesMap.put(id, wrapper);
  }

  @Override
  public void createCar(int carId, TrafficDirection dir) {
    TextVehicle textVehicle;
    if (dir == TrafficDirection.NORTH) {
      textVehicle= new TextVehicle(
              (char) ('a' + carId),
              TextPath.CAR_NORTH_START_POINT,
              TextPath.CAR_NORTH_PATH
      );
    } else {
      textVehicle= new TextVehicle(
              (char) ('a' + carId),
              TextPath.CAR_SOUTH_START_POINT,
              TextPath.CAR_SOUTH_PATH
      );
    }

    addVehicle(carId, textVehicle);
  }

  @Override
  public void destroyCar(int carId) {

  }

  @Override
  public void setCarDynamic(int carId, boolean isDynamic) {

  }

  @Override
  public double getCarProgress(int carId) {
    return 0;
  }

  @Override
  public void setCarProgress(int carId, double dur) {

  }

  @Override
  public void setLightColor(int id, TrafficColor color) {
    // e.g. GREEN::TrafficColor -> "GREEN"::String -> 'G'::char
    char c = color.toString().charAt(0);
    lightsMap.put(id, c);
  }

  @Override
  public void setTramDynamic(int tramId, boolean isDynamic) {
    vehiclesMap.get(tramId).isPaused = !isDynamic;
  }

  @Override
  public double getTramProgress(int tramId) {
    return (double) vehiclesMap.get(tramId).textVehicle.getProgress();
  }

  // TODO: Automatically calculate it from the path's "displacement points"
  // TODO: Move to TextPath class?
  final static double TOTAL_DURATION = 135.0;
  @Override
  public void setTramProgress(int tramId, double progress) {
    if (progress < 0) {
      progress = progress + TOTAL_DURATION;
    }
    vehiclesMap.get(tramId).textVehicle.setProgress((int) progress);
  }

  @Override
  public void setTramProgress(int tramId, String progressPoint) {
    int segment = progressPoint.charAt(8);
    int progress;
    if (progressPoint.endsWith("start")) {
      progress = TextPath.TRAM_PATH_SEGMENTS[segment][0];
    } else { // end
      progress = TextPath.TRAM_PATH_SEGMENTS[segment][1];
    }

    setTramProgress(tramId, progress);
  }

  final static double DELTA_DURATION = 1.0;
  @Override
  public double getDeltaConstant() {
    return DELTA_DURATION;
  }

  @Override
  public void startAll() {

    // Add placeholders for lights
    for (int id = 0; id < 10; id++) {
      lightsMap.put(id, 'Y'); // should be '?'
    }

    // start "redrawer" (redraw timer)
    redrawer = new Timer();
    redrawer.schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                redraw();
              }
            },
            0,
            250 // 0.25 sec
    );

  }

  @Override
  public void stopAll() {
    redrawer.cancel();

    // cancel all tickers
    vehiclesMap.forEach((i, wrappedVehicle) -> {
      wrappedVehicle.ticker.cancel();
    });
  }

  void redraw() {
    String view = toAsciiView();
    if (fancy) {
      view = toEmojiView(view);
    }

    // PRINT
    System.out.println("\033\143"); // to clear the screen on Linux
    System.out.println(view);
    TextVehicle v = vehiclesMap.get(0).textVehicle;
    System.out.println(String.format(
        "Tram %c progress=%d  point%s  graphicSegment=%d  mapIndex=%d",
          v.letter, v.getProgress(), v.p.toString(), getGraphicSegment(0), toMapIndex(v.p)
    ));
  }

  String toAsciiView() {
    // See https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
    StringBuilder view = new StringBuilder(VIEW_TEMPLATE);

    // TRAMS & CARS
    vehiclesMap.forEach((i, wrappedVehicle) -> {
      TextVehicle v = wrappedVehicle.textVehicle;
      int viewIndex = toMapIndex(v.p);
      view.setCharAt(viewIndex, v.letter);
    });

    // LIGHT COLORS
    for (int lightId = 0; lightId < 10; lightId++) {
      // e.g. finds "1:?" (as in "[1:?]") to replace "?" (like "[1:G]")
      int viewIndex = view.indexOf(lightId + ":") + 2;
      // System.out.println("lightId " + lightId + " at " + viewIndex);
      char c = lightsMap.get(lightId);
      view.setCharAt(viewIndex, c);
    }

    return view.toString();
  }

  /** Map ascii characters to emoji (see the legend in `map.txt`) */
  String toEmojiView(String asciiView) {
    String view = asciiView
        // replace color characters with hearts
        // black, green, red, yellow heart
        .replaceAll(":\\?", ":\uD83D\uDDA4️️️")
        .replaceAll(":G", ":\uD83D\uDC9A️")
        .replaceAll(":R", ":❤️")
        .replaceAll(":Y", ":\uD83D\uDC9B️️")

        // replace trams with tram emojis
        .replaceAll("[A-Z]", "\uD83D\uDE8B️")
        // replace cars with car emojis
        .replaceAll("[a-z]", "\uD83D\uDE97️")

        // replace the rail characters {'-', '+', '/', '|,' '\'} with black squares
        .replaceAll("[+\\-\\\\/\\|]", "⬛️️")

        // TODO: Use "[^Emoji]" or "[anyAsciiCharater]"
        // replace the rest with white squares
        .replaceAll("[0-9 .()\\[\\]\\:]", "⬜️")
        ;
        
    return view;
  }

  int toMapIndex(Point point) {
    // FIXME: Correct?
    int MAP_WIDTH = 70;
    return MAP_WIDTH * point.y + point.x;
  }

}

class Wrapper {
  Timer ticker = null;
  boolean isPaused = false;
  TextVehicle textVehicle = null;
}

class TextVehicle {
  Point p; // computed value
  char letter;
  Point[] path;
  Point startingPoint;
  private int progress;

  TextVehicle(char letter, Point startingPoint, Point[] path) {
    this.letter = letter;
    this.startingPoint = startingPoint;
    this.path = path;
    this.setProgress(0);
  }

  int getProgress() {
    return progress;
  }

  void setProgress(int val, boolean isRelative) {
    if (isRelative) {
      val = this.progress + val;
    }
    this.progress = val;
    this.p = calculateProgressPoint(this.path, this.startingPoint, this.progress);
  }

  void setProgress(int val) {
    setProgress(val, false);
  }

  // FIXME: Should be `static`?
  static Point calculateProgressPoint(Point[] path, Point startingPoint, int progressVal) {
    Point p = startingPoint.clone();

    for (Point displacement : path) {
      // FIXME: Find a better way to use "displacement points" without cloning and mutating them
      displacement = displacement.clone();

      while ( progressVal > 0 && (displacement.x != 0 || displacement.y != 0) ) {
        progressVal--;

        if (displacement.x < 0) {
          p.x--;
          displacement.x++;
        } else if (displacement.x > 0) {
          p.x++;
          displacement.x--;
        }

        if (displacement.y < 0) {
          p.y--;
          displacement.y++;
        } else if (displacement.y > 0) {
          p.y++;
          displacement.y--;
        }
      }

    }
    return p;
  }

  @Override
  public String toString() {
    return String.format("%c(%d, %d)",
        letter, p.x, p.y
       );
  }

}

class Point {
  int x;
  int y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public Point clone() {
    return new Point(this.x, this.y);
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }
}

class TextPath {

  // TRAM

  static Point TRAM_START_POINT = new Point(3, 9);
  static Point[] TRAM_PATH = {
          // startToBridge
          new Point(+18, 0), new Point(+3, -3),
          // bridge
          new Point(+12, 0),
          // bridgeToIntersection
          new Point(+3, +3), new Point(+19, 0),
          // intersectionToIntersection
          new Point(+6, 0), new Point(+1, -1), new Point(0, -4), new Point(-1, -1), new Point(-3, 0),
          // intersectionToBridgeReverse
          new Point(-22, 0), new Point(-3, +3),
          // bridgeReverse
          new Point(-12, 0),
          // bridgeReverseToStart
          new Point(-3, -3), new Point(-18, 0),
          new Point(-1, +1), new Point(-1, +1), new Point(0, +3), new Point(+1, +1), new Point(+1, -1)
  };

  static int[][] TRAM_PATH_SEGMENTS =
        {
              {00, 21}, // startToBridge
              {21, 55}, // bridge, bridgeToIntersection
              {55, 70}, // intersectionToIntersection
              {70, 135} // intersectionToBridgeReverse, bridgeReverse, bridgeReverseToStart
        };

  // CAR
  static int[][] CAR_PATH_SEGMENTS =
          {
                {0, 2},
                {2, 10},
          };

  // CAR GOING NORTH
  static Point CAR_NORTH_START_POINT = new Point(57, 11);
  static Point[] CAR_NORTH_PATH = {
          new Point(0, -2), // before intersection
          new Point(0, -8)  // after intersection
  };

  // CAR GOING SOUTH
  static Point CAR_SOUTH_START_POINT = new Point(56, 1);
  static Point[] CAR_SOUTH_PATH = {
          new Point(0, +2), // before intersection
          new Point(0, +8)  // after intersection
  };

}

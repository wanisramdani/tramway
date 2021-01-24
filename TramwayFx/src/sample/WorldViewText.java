package sample;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class WorldViewText implements WorldViewInterface {

  final static String VIEW_TEMPLATE =
          "                                     [9:R]..[8:Y] (7:G)....[6:R]...  \n" +
          "                                                       |  |       .  \n" +
          "                                                       |  |       .  \n" +
          "   +----------------+                +-----------------+--+--+    .  \n" +
          "  /                  \\              /                  |  |   \\ [5:Y]\n" +
          " /                    \\            /                   |  |   |      \n" +
          " |                     ^^^^^^^^^^^^                    |  |   |      \n" +
          " \\                    /            \\                   |  |   |      \n" +
          "  \\                  /              \\                  |  |   /      \n" +
          "   +----------------+                +-----------------+--+--+       \n" +
          "                                                       |  |          \n" +
          "                                                       |  |          \n" +
          "         [0:G]...[1:G]                [2:Y].......[3:R]....(4:G)     \n" +
          "                                                                     " ;

  Map<Integer, Character> lightsMap = Collections.synchronizedMap(new HashMap<>());
  Map<Integer, TextWrapper> vehiclesMap = Collections.synchronizedMap(new HashMap<>());
  Timer redrawer;

  // TODO: Support different levels of "fancy": messages, ascii art, emoji, pixel art, etc.
  /** Toggles emojiView mode */
  boolean fancy = false;

  @Override
  public void startAll() {

    // Add placeholders for lights
    for (int id = 0; id < 10; id++) {
      lightsMap.put(id, '?');
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

  @Override
  public void setLightColor(int id, TrafficColor color) {
    // e.g. GREEN::TrafficColor -> "GREEN"::String -> 'G'::char
    char c = color.toString().charAt(0);
    lightsMap.put(id, c);
  }

  @Override
  public double getDeltaConstant() {
    return DELTA_DURATION;
  }

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
    createVehicle(tramId, textVehicle);
  }

  @Override
  public void destroyTram(int tramId) {
    destroyVehicle(tramId);
  }

  @Override
  public void setTramDynamic(int tramId, boolean isDynamic) {
    vehiclesMap.get(tramId).isPaused = !isDynamic;
  }

  @Override
  public double getTramProgress(int tramId) {
    return vehiclesMap.get(tramId).textVehicle.getProgress();
  }

  // TODO: Automatically calculate TOTAL_DURATION from the path's "displacement points"
  // TODO: Move to TextPath class
  /** The total duration a vehicle needs to make a complete animation in its path */
  final static double TOTAL_DURATION = 130.0;

  /** Min allowed difference in progress between two vehicles in the same segment */
  final static double DELTA_DURATION = 1.0;

  @Override
  public void setTramProgress(int tramId, double progress) {
    if (progress < 0) {
      progress = progress + TOTAL_DURATION;
    }
    vehiclesMap.get(tramId).textVehicle.setProgress((int) progress);
  }

  @Override
  public void setTramProgress(int tramId, String namedProgress) {
    // e.g. "segment_1_end" -> '1' -> 1
    int segment = namedProgress.charAt(8) - '0';
    int progress;
    if (namedProgress.endsWith("start")) {
      progress = TextPath.TRAM_PATH_SEGMENTS[segment][0];
    } else { // "end"
      progress = TextPath.TRAM_PATH_SEGMENTS[segment][1];
    }

    setTramProgress(tramId, progress);
  }

  @Override
  public void createCar(int carId, TrafficDirection dir) {
    TextVehicle textVehicle;
    if (dir == TrafficDirection.NORTH) {
      textVehicle = new TextVehicle(
          (char) ('a' + carId),
          TextPath.CAR_NORTH_START_POINT,
          TextPath.CAR_NORTH_PATH
      );
    } else {
      textVehicle = new TextVehicle(
          (char) ('a' + carId),
          TextPath.CAR_SOUTH_START_POINT,
          TextPath.CAR_SOUTH_PATH
      );
    }

    createVehicle(carId, textVehicle);
  }

  @Override
  public void destroyCar(int carId) {
    destroyVehicle(carId);
  }

  @Override
  public void setCarDynamic(int carId, boolean isDynamic) {
    vehiclesMap.get(carId).isPaused = !isDynamic;
  }

  @Override
  public double getCarProgress(int carId) {
    return vehiclesMap.get(carId).textVehicle.getProgress();
  }

  @Override
  public void setCarProgress(int carId, double dur) {
    setTramProgress(carId, dur); // TODO: create setVehicleProgress
  }

  @Override
  public void setCarProgress(int carId, String namedProgress) {
    // TODO
  }

  void createVehicle(int id, TextVehicle v) {
    TextWrapper wrapper = new TextWrapper();

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
        200
    );
    wrapper.ticker = ticker;

    vehiclesMap.put(id, wrapper);
  }

  void destroyVehicle(int id) {
    vehiclesMap.get(id).ticker.cancel();
    vehiclesMap.remove(id);
  }

  void redraw() {
    String view = toAsciiView();
    if (fancy) {
      view = toEmojiView(view);
    }

    // PRINT
    System.out.println("\033\143"); // to clear the screen on Linux
    System.out.println(view);
    // DEBUGGING
    if (vehiclesMap.size() > 0) {
      TextVehicle v = vehiclesMap.get(0).textVehicle;
      System.out.println(String.format(
          "Tram %c progress=%d  point%s  graphicSegment=%d  mapIndex=%d",
          v.letter, v.getProgress(), v.p.toString(), getGraphicSegment(0), toMapIndex(v.p)
      ));
    }
  }

  String toAsciiView() {
    StringBuilder view = new StringBuilder(VIEW_TEMPLATE);

    // TRAMS & CARS
    vehiclesMap.forEach((i, wrappedVehicle) -> {
      TextVehicle v = wrappedVehicle.textVehicle;
      int viewIndex = toMapIndex(v.p);
      view.setCharAt(viewIndex, v.letter);
    });

    // LIGHTS
    for (int lightId = 0; lightId < 10; lightId++) {
      // e.g. finds "1:?" (as in "[1:?]") to replace "?" (like "[1:G]")
      int viewIndex = view.indexOf(lightId + ":") + 2;
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
        // replace the bridge characters
        .replaceAll("\\^", "🌉️️")

        // TODO: Use "[^Emoji]" or "[anyAsciiCharater]"
        // replace the rest with white squares
        .replaceAll("[0-9 .()\\[\\]\\:]", "⬜️")
        ;

    return view;
  }

  int toMapIndex(Point point) {
    int MAP_WIDTH = 70;
    return MAP_WIDTH * point.y + point.x;
  }

}

class TextWrapper {
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

  // FIXME: Should not be `static`?
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
      new Point(+17, 0), new Point(+3, -3),
      // bridge
      new Point(+11, 0),
      // bridgeToIntersection
      new Point(+3, +3), new Point(+16, 0),
      // intersectionToIntersection
      new Point(+8, 0), new Point(+1, -1), new Point(0, -4), new Point(-1, -1), new Point(-3, 0),
      // intersectionToBridgeReverse
      new Point(-21, 0), new Point(-3, +3),
      // bridgeReverse
      new Point(-11, 0),
      // bridgeReverseToStart
      new Point(-3, -3), new Point(-17, 0),
      new Point(-1, +1), new Point(-1, +1), new Point(0, +2), new Point(+1, +1), new Point(+1, +1)
  };

  static int[][] TRAM_PATH_SEGMENTS =
      {
          {00, 20}, // startToBridge
          {20, 50}, // bridge, bridgeToIntersection
          {50, 67}, // intersectionToIntersection
          {67, 130} // intersectionToBridgeReverse, bridgeReverse, bridgeReverseToStart
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

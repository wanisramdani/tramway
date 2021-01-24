# TODO

- MAYBE to be notified of Trams' animation progress:
[KeyFrame](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/KeyFrame.html)
or maybe [ObservableList](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html).

```
USAGE: tramway.jar [MODE]

MODES
  0 - Debug messages
  1 - ASCII
  2 - Emoji
  3 - Pixel
  4 - JavaFX (default)
```

---

```java
// WorldViewText

public static final int DEBUG_MODE = 0;
public static final int ASCII_MODE = 1;
public static final int EMOJI_MODE = 2;
public static final int PIXEL_MODE = 3; // or RETRO_MODE

new WorldViewText(WorldViewRetro.PIXEL_MODE);
```

---

MASK
----
```
                              ░░░░░░░░                                         
                              ░░░░░░░░   [9:R]..[8:Y] (7:G)....[6:R].......    
                              ░░░░░░░░                     │██│           .    
                              ░░░░░░░░                     │██│           .    
  P════════════════════════O  ░░░░░░░░  L══════════════════K██╪J══════I   .    
  ║                        ║  ░░░░░░░░  ║                  │██│       ║ [5:Y]  
  ║                        N  ░░░░░░░░  M                  │██│       ║        
  ║                        C^^^^^^^^^^^^D                  │██│       ║        
  ║                        ║  ░░░░░░░░  ║                  │██│       ║        
  Q                        ║  ░░░░░░░░  ║                  │██│       ║        
  A════════════════════════B  ░░░░░░░░  E═════════════════F╪██G═══════H        
                              ░░░░░░░░                     │██│                
                              ░░░░░░░░                     │██│                
               [0:G]...[1:G]  ░░░░░░░░    [2:Y].......[3:R]....(4:G)           
                              ░░░░░░░░                                         
```

flags: 00-000 (2 bits for logicSegment + displayed, entered, and left bits)
logicSegment: 0
Z is displayed ? nothing : createTram
Z in part 1 && entered? nothing : enter() (`canAdvance.release()` it)
Z in part > 2 && left? nothing : leave() (`canAdvance.release()` it)

logicSegment == 0 && graphicSegment == 0
graphicSegment > logicSegment: pause()
logicSegment > graphicSegment: resume()

AB: 0.0
BC: 0.1
CD: 0.2

DE: 1.0
EF: 1.1
FG: 1.2

GHI: 2.0
IJ: 2.1
JK: 2.2

KL: 3.0
LM: 3.1
MN: 3.2
N-O-P-Q: 3.4

---

```java
int autoRedrawInterval = 150; // millis

// FIXME: MAYBE must not be hard-coded! Maybe `final int WIDTH = MAP.indexOf('\n') + 1;`
// Width 80 is a safe default especially for old, non-resizable consoles like Windows XP's.
final int MAP_WIDTH = 80;

Point toPoint(int index) {
  int y = (int) (index / MAP_WIDTH);
  int x = index % MAP_WIDTH;
  return new Point(x, y);
}

int toIndex(Point p) {
  return p.x + p.y * MAP_WIDTH;
}

Point calculateDisplacement(Point start, Point end) {
  return new Point(end.x - start.x, end.y - start.y);
}

int calculateTotal(Point[] path) {
  int totalProgress;
  for (Point p : path) {
    int delta = p.x == 0 ? y : x;
    delta = delta > 0 ? delta : -delta;
    totalProgress += delta;
  }
  return totalProgress;
}

static char currentTramLetter = 'A';
static char currentCarLetter = 'a';

char letter = currentTramLetter;
currentTramLetter = (currentTramLetter + 1) % 'Z';

char letter = currentCarLetter;
currentCarLetter = (currentCarLetter + 1) % 'z';


/**
 * The map. See the legend in 'map.txt'
 *
 * See: https://en.wikipedia.org/wiki/Box-drawing_character
 */

/**
 * Returns an updated ascii map with colored traffic light signs and blue river.
 * Note: Not platform independent as it relies on Bash color escape characters!
 *
 * For COLOR_MODE
 *
 * See: https://unix.stackexchange.com/a/37274
 * See: https://en.wikipedia.org/wiki/Box-drawing_character
 */
String toColorView(String asciiView) {
  String NONE = "\033[00m";
  String RED = "\033[01;31m";
  String GREEN = "\033[01;32m";
  String YELLOW = "\033[01;33m";
  String BLUE = "\033[01;44m";

  // TODO: "()|" -> "╼|" and "|()" -> "|╾"

  String view = asciiView
    .replaceAll("░", "${BLUE}░${NONE}")

    // e.g. "[0:G]" > "[0:G╿${NONE}" > "[0:${GREEN}╿${NONE}" > "...${GREEN}╿${NONE}"
    .replaceAll("]", "╿${NONE}")
    .replaceAll(")", "╾${NONE}")
    .replaceAll(":R", ":${RED}")
    .replaceAll(":G", ":${GREEN}")
    .replaceAll(":Y", ":${YELLOW}")

     // Extend logic connectors if any exist. e.g. ".[i:" or ".(j:" become "...."
    .replaceAll(" ..:", "    ")
    .replaceAll("\\...:", "....");

  return view;
}

// ---

class TextMap {

  private TextMap() {}

  getPoint(char c): getPoint(c, 0, 0);

  /**
  * Example: getPoint('M', 0, +1) // to make 'M' = 'D'
  */
  Point getPoint(char c, int translateX, int translateY);
}
```

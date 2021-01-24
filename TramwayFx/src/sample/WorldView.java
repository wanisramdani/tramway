package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WorldView implements WorldViewInterface {
    boolean shouldLoop = false;

    public WorldView() {}

    @FXML
    public AnchorPane theWorld;

    @FXML
    public GridPane gridPane;

    @FXML
    public Hyperlink readMeOnGithub;

    // Declaration
    @FXML
    private Path tramPath;

    @FXML
    public Path southToNorth;

    @FXML
    public Path northToSouth;

    @FXML
    private Rectangle tram;

    @FXML
    private Rectangle alphaCar;

    @FXML
    private Rectangle betaCar;

    @FXML
    private MoveTo endPoint;

    @FXML
    private ArcTo leftArc;

    // Section 0: West-South
    @FXML
    private MoveTo startPoint;

    @FXML
    private LineTo startLine;

    @FXML
    private MoveTo startOfWestSouthCross;

    @FXML
    private LineTo westSouthCross;

    @FXML
    private MoveTo startOfWestBridge;

    @FXML
    private LineTo westBridge;

    @FXML
    private MoveTo endOfWestBridge;

    // Section 1: East-South
    @FXML
    private LineTo eastSouthCross;

    @FXML
    private MoveTo endOfEastSouthCross;

    @FXML
    private LineTo endOfWestLine;

    @FXML
    private MoveTo endOfWestSide;

    @FXML
    private ArcTo rightArc;

    // Section 2 East-North
    @FXML
    private MoveTo startOfEastSide;

    @FXML
    private LineTo eastLine;

    @FXML
    private MoveTo startOfEastNorthCross;

    @FXML
    private LineTo eastNorthCross;

    @FXML
    private MoveTo startOfEastBridge;

    @FXML
    private LineTo eastBridge;

    @FXML
    private MoveTo endOfEastBridge;

    // Section 3: West-North
    @FXML
    private LineTo westNorthCross;

    @FXML
    private MoveTo startOfLastLine;

    @FXML
    private LineTo endOfEastLine;

    @FXML
    private Path roadPath;

    @FXML
    private MoveTo southRoad;

    @FXML
    private LineTo goNorth;

    @FXML
    private MoveTo northRoad;

    @FXML
    private LineTo goSouth;

    @FXML
    private MoveTo eastRoadBorder;

    @FXML
    private LineTo eastRoadLine;

    @FXML
    private MoveTo westRoadBorder;

    @FXML
    private LineTo westRoadLine;

    @FXML
    public LineTo finalLine;

    @FXML
    private Circle downCarLight;

    @FXML
    private Circle upCarLight;

    @FXML
    private Line linkZeroFirstLight;

    @FXML
    private Line linkSecondThirdLight;

    @FXML
    private Line linkForthFifthLight;

    @FXML
    private Line linkSixthSeventhLight;

    @FXML
    private Text status;

    @FXML
    private Rectangle light_0;

    @FXML
    private Rectangle light_1;

    @FXML
    private Rectangle light_2;

    @FXML
    private Rectangle light_3;

    @FXML
    private Rectangle light_4;

    @FXML
    private Rectangle light_5;

    @FXML
    private Rectangle light_6;

    @FXML
    private Rectangle light_7;

    @FXML
    public Button pauseButton;

    @FXML
    public Button playButton;

    @FXML
    public Button resetButton;

    WorldControllerInterface worldController;

    final int TRAM_TOTAL_DURATION = 10000;
    static final int TRAM_DELTA = 250;
    static final int CAR_DELTA = 200;
    final int CAR_TOTAL_DURATION  =  1000;

    Path carPath;
    HashMap<String, Wrapper> vehicles  = new HashMap<String, Wrapper>();

    public void addEventListeners(){
        playButton.setOnAction(action -> {
            playButton.setDisable(true);
            pauseButton.setDisable(false);
            playAll();
        });

        pauseButton.setOnAction(action -> {
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            pauseAll();
        });

        resetButton.setOnAction(action -> {
            playButton.setDisable(false);
            pauseButton.setDisable(false);
            restAll();
        });
    }

    @Override
    public double getDeltaConstant() {
        return TRAM_DELTA;
    }

    @Override
    public int getGraphicSegment(int tramId) {
        /* CAN USE:
        PathTransition pt = vehicles.get("tram_" + tramId).pathTransition;
        ObservableMap<String, Duration> points = pt.getCuePoints();
        */
        double dur = getTramProgress(tramId);
        for (int i = 0; i < 5; i++) {
            double start = getCuePoint("segment_" + i + "_start");
            double end = getCuePoint("segment_" + i + "_end");
            if (dur >= start && dur <= end) {
                return i;
            }
        }
        throw new IllegalStateException("Uncovered interval: " + dur);
    }

    private double getCuePoint(String s) {
        switch (s) {
            case "segment_0_start":
                return 0.0; //startPoint.getX();
            case "segment_0_end":
                return 1460.0; //startLine.getX();

            case "segment_1_start":
                return 1460.0; //endOfEastSouthCross.getX();
            case "segment_1_end":
                return 3370.0; //endOfWestLine.getX();

            case "segment_2_start":
                return 3370; //startOfEastSide.getX();
            case "segment_2_end":
                return 5160; //eastLine.getX();

            case "segment_3_start":
                return 5160; //startOfLastLine.getX();
            case "segment_3_end":
                return 6960; //endOfEastLine.getX();

            case "segment_4_start":
                return 6960;
            case "segment_4_end":
                return 10000;
            default: return 0.0;
        }

    }

    @Override
    public void setTramDynamic(int tramId, boolean isDynamic) {
        if (isDynamic) {
            vehicles.get("tram_" + tramId).pathTransition.play();
        } else {
            vehicles.get("tram_" + tramId).pathTransition.pause();
        }
    }

    public void playAll() {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get("tram_" + i) != null) {
                vehicles.get("tram_" + i).pathTransition.play();
            }
            if (vehicles.get("car_" + i) != null) {
                vehicles.get("car_" + i).pathTransition.play();
            }
        }
    }

    public void pauseAll() {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get("tram_" + i) != null) {
                setTramDynamic(i, false);
            }
            if (vehicles.get("car_" + i) != null) {
                setCarDynamic(i, false);
            }
        }
    }

    public void restAll() {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get("tram_" + i) != null) {
                destroyTram(i);
            }
            if (vehicles.get("car_" + i) != null) {
                destroyCar(i);
            }
        }
        vehicles.clear();
    }

    @Override
    public void setTramProgress(int tramId, double duration) {
        if (duration < 0) {
            duration = duration + TRAM_TOTAL_DURATION;
        }
        vehicles.get("tram_" + tramId).pathTransition.jumpTo(Duration.millis(duration));
    }

    @Override
    public void setTramProgress(int tramId, String namedDuration) {
        double where = getCuePoint(namedDuration);
        vehicles.get("tram_" + tramId).pathTransition.jumpTo(Duration.millis(where));
    }

    @Override
    public double getTramProgress(int tramId) {
        return Double.parseDouble( String.valueOf( vehicles.get("tram_" + tramId).pathTransition.getCurrentTime().toMillis() ) );
    }
    // getTramProgress(id): getCurrentTime() / TRAM_TOTAL_DURATION * 100;
    // setTramProgress(id, percentage): getCurrentTime() / TRAM_TOTAL_DURATION * 100;

    @Override
    public void createTram(int tramId) {
        Rectangle r = new Rectangle();
        r.setX(10);
        r.setY(320);
        r.setWidth(49);
        r.setHeight(30);
        //r.setFill(Color.web("#2ddd0a")); r.setStroke(Color.BLACK); r.setStrokeType(StrokeType.INSIDE);
        r.setFill(new ImagePattern(new Image("sample/resources/tram.png")));
        r.setId("tram_" + tramId);
        r.setArcHeight(5);
        r.setArcWidth(5);
        gridPane.getChildren().add(r);

        PathTransition pt = new PathTransition();
        Wrapper w = new Wrapper(r, pt);

        followPath(w, tramPath, TRAM_TOTAL_DURATION);

        vehicles.put("tram_" + tramId, w);
    }

    public void followPath(Wrapper t, Path path, int targetSpeed) {
        t.pathTransition.setDuration(Duration.millis(targetSpeed));
        t.pathTransition.setNode(t.shape);
        t.pathTransition.setPath(path);
        t.pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        t.pathTransition.setInterpolator(Interpolator.LINEAR);

        if (shouldLoop) {
            t.pathTransition.onFinishedProperty().set(
                    (ActionEvent event) -> {
                        t.pathTransition.play();
                    }
            );
        }
    }

    @Override
    public void destroyTram(int tramId) {
        gridPane.getChildren().remove(vehicles.get("tram_" + tramId).shape);
        //vehicles.remove("tram_" + tramId);
    }

    @Override
    public void setCarDynamic(int carId, boolean isDynamic) {
        if (isDynamic) {
            vehicles.get("car_" + carId).pathTransition.play();
        } else {
            vehicles.get("car_" + carId).pathTransition.pause();
        }
    }

    static ArrayList<Color> carColors = new ArrayList<Color>(Arrays.asList(
            Color.DODGERBLUE,
            Color.web("#770ec3"),
            Color.ORANGE,
            Color.GOLD,
            Color.AQUA
    ));
    static ArrayList<Image> carImage = new ArrayList<Image>(Arrays.asList(
            new Image("sample/resources/flatbed-covered.png"),
            new Image("sample/resources/city-car.png"),
            new Image("sample/resources/flatbed-covered.png"),
            new Image("sample/resources/city-car.png")
            // ...
    ));

    @Override
    public void createCar(int carId, TrafficDirection dir) {
        Rectangle car = new Rectangle();
        //Color color = carColors.remove(0); // Like carColors.pop() if it were q queue
        Image carIcon = carImage.remove(0);
        if (dir == TrafficDirection.NORTH) {
            car.setX(0);
            car.setY(310);
            car.setFill(new ImagePattern(carIcon));
            carPath = southToNorth;
        } else {
            car.setX(0);
            car.setY(330);
            car.setFill(new ImagePattern(carIcon));
            carPath = northToSouth;
        }

        car.setWidth(40);
        car.setHeight(50);
        car.setRotate(90);
        //car.setStroke(Color.BLACK);car.setStrokeType(StrokeType.INSIDE);
        car.setId("car_" + carId);
        car.setArcHeight(5);
        car.setArcWidth(5);

        Wrapper w = new Wrapper(car, new PathTransition());
        followPath(w, carPath, CAR_TOTAL_DURATION);

        gridPane.getChildren().add(car);
        vehicles.put("car_" + carId, w);
    }

    @Override
    public void destroyCar(int carId) {
        //carColors.add((Color) vehicles.get("car_" + carId).shape.getFill()); // to return the popped color
        gridPane.getChildren().remove(vehicles.get("car_" + carId).shape);
        //vehicles.remove("car_" + carId);
    }

    @Override
    public double getCarProgress(int carId) {
        Wrapper w = vehicles.get("car_" + carId);
        //HACK
        if (w == null) {
            return CAR_TOTAL_DURATION;
        } else {
            return w.pathTransition.getCurrentTime().toMillis();
        }
    }

    @Override
    public void setCarProgress(int carId, double duration) {
        if (duration < 0) {
            duration = duration + CAR_TOTAL_DURATION;
        }
        vehicles.get("car_" + carId).pathTransition.jumpTo(Duration.millis(duration));
    }

    @Override
    public void setCarProgress(int carId, String namedProgress) {
        // TODO maybe
    }

    @Override
    public void startAll() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    worldController.updateView();
                } catch (Exception e ) {
                    // OvO
                }
            }
        };
        timer.start();
    }

    @Override
    public void stopAll() {
        // NOTHING?
    }

    @Override
    public void setLightColor(int id, TrafficColor color) {
        Shape target;
        Color fxColor;

        switch (color) {
            case RED: fxColor = Color.RED; break;
            case GREEN: fxColor = Color.LAWNGREEN; break;
            case YELLOW: fxColor = Color.YELLOW; break;
            default:
                throw new IllegalStateException("Unexpected value: " + color);
        }

        switch (id) {
            case 0: target = light_0; break;
            case 1: target = light_1; break;
            case 2: target = light_2; break;
            case 3: target = light_3; break;
            case 4: target = downCarLight; break;
            case 5: target = light_4; break;
            case 6: target = light_5; break;
            case 7: target = upCarLight; break;
            case 8: target = light_6; break;
            case 9: target = light_7; break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }

        target.setFill(fxColor);
    }

}

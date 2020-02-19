package sample;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashMap;


public class WorldView implements WorldViewInterface{
    @FXML
    public AnchorPane theWorld;
    @FXML
    public GridPane gridPane;

    public WorldView () {
    }

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

    final int TRAMSPEED = 10000;
    final int CARSPEED = 1000;

    Path carPath;
    HashMap<String, Wrapper> vehicles  = new HashMap<String, Wrapper>();

    PathTransition tramTransition = new PathTransition();
    PathTransition alphaCarTransition = new PathTransition();
    PathTransition betaCarTransition = new PathTransition();
    public PathTransition[] allTransitions = new PathTransition[]{tramTransition, alphaCarTransition, betaCarTransition};

    public void startAnimate() {
        createTram(0);
        setTramDynamic(0, true);
        setTramProgress(0, 1000);

        createTram(1);
        setTramDynamic(1, true);

        createCar(0, TrafficDirection.NORTH);
        setCarDynamic(0, true);


        createCar(1, TrafficDirection.SOUTH);
        setCarDynamic(1, true);

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
            playButton.setDisable(true);
            pauseButton.setDisable(false);
            restAll();
        });

    }

    // Method requires tramId as parameter
    @Override
    public double getDeltaConstant() {
        return TRAMSPEED - getTramProgress(0);
    }

    // There is miscalculation: error caused by getTramProgress, it returns Uncovered interval
    @Override
    public int getGraphicSegment(int tramId) {
        double dur = getTramProgress(tramId);
        for (int i = 0; i < 4; i++) {
            double start = getCuePoint("segment_" + i + "_start");
            double end = getCuePoint("segment_" + i + "_end");
            System.out.println(start + " " + end); // for debugging
            if (dur >= start && dur <= end) {
                return i;

            }
        }
        throw new InvalidStateException("Uncovered interval: " + dur);
    }

    private double getCuePoint(String s) {
        switch (s) {
            case "segment_0_start":
                return startPoint.getX();
            case "segment_0_end":
                return startLine.getX();

            case "segment_1_start":
                return endOfEastSouthCross.getX();
            case "segment_1_end":
                return endOfWestLine.getX();

            case "segment_2_start":
                return startOfEastSide.getX();
            case "segment_2_end":
                return eastLine.getX();

            case "segment_3_start":
                return startOfLastLine.getX();
            case "segment_3_end":
                return endOfEastLine.getX();

        }

        return 0.0;
    }

    @Override
    public void setTramDynamic(int tramId, boolean isDynamic) {
        if (isDynamic) {
            followPath(vehicles.get("tram_" + tramId), tramPath, TRAMSPEED);
        } else {
            vehicles.get("tram_" + tramId).pathTransition.pause();
        }
    }


    public void followPath(Wrapper t, Path path, int targetSpeed) {
        t.pathTransition.setDuration(Duration.millis(targetSpeed));
        t.pathTransition.setNode(t.shape);
        t.pathTransition.setPath(path);
        t.pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

        t.pathTransition.onFinishedProperty().set(
                (ActionEvent event) -> {
                    t.pathTransition.play();
                }
        );

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
                status.setText("Duration: " + getTramProgress(i));
                setTramDynamic(i, false);
            }
            if (vehicles.get("car_" + i) != null) {
                setCarDynamic(i, false);

            }
            // System.out.println(getGraphicSegment(i));

        }
    }

    public void restAll() {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get("tram_" + i) != null) {
                deleteTram(i);
            }
            if (vehicles.get("car_" + i) != null) {
                deleteCar(i);
            }
        }
        startAnimate();
    }

    @Override
    public void setTramProgress(int tramId, double duration) {
        if (duration < 0) {
            duration = duration + TRAMSPEED;
        }
        vehicles.get("tram_" + tramId).pathTransition.jumpTo(Duration.millis(duration));
    }


    @Override
    public void setTramProgress(int tramId, String namedDuration) {

    }

    @Override
    public double getTramProgress(int tramId) {
        return Double.parseDouble( String.valueOf( vehicles.get("tram_" + tramId).pathTransition.getCurrentTime().toMillis() ) );
    }

    @Override
    public void createTram(int tramId) {
        Rectangle r = new Rectangle();
        r.setX(10);
        r.setY(320);
        r.setWidth(49);
        r.setHeight(30);
        r.setFill(Color.web("#2ddd0a"));
        r.setStroke(Color.BLACK);
        r.setStrokeType(StrokeType.INSIDE);
        r.setId("tram_" + tramId);
        r.setArcHeight(5);
        r.setArcWidth(5);
        gridPane.getChildren().add(r);
        vehicles.put("tram_" + tramId, new Wrapper(r, new PathTransition()));
    }


    @Override
    public void deleteTram(int tramId) {
        gridPane.getChildren().remove(vehicles.get("tram_" + tramId).shape);
        vehicles.remove("tram_" + tramId);
    }

    @Override
    public void setCarDynamic(int carId, boolean isDynamic) {
        if (isDynamic) {
            followPath(vehicles.get("car_" + carId), carPath, CARSPEED);
        } else {
            vehicles.get("car_" + carId).pathTransition.pause();
        }
    }

    @Override
    public void createCar(int carId, TrafficDirection dir) {
        Rectangle car = new Rectangle();
        if (dir == TrafficDirection.NORTH) {
            car.setX(0);
            car.setY(310);
            car.setFill(Color.DODGERBLUE);
            carPath = southToNorth;
        } else {
            car.setX(0);
            car.setY(330);
            car.setFill(Color.web("#770ec3"));
            carPath = northToSouth;
        }
        car.setWidth(43);
        car.setHeight(30);
        car.setRotate(90);
        car.setStroke(Color.BLACK);
        car.setStrokeType(StrokeType.INSIDE);
        car.setId("car_" + carId);
        car.setArcHeight(5);
        car.setArcWidth(5);
        gridPane.getChildren().add(car);
        vehicles.put("car_" + carId, new Wrapper(car, new PathTransition()));
    }

    @Override
    public void deleteCar(int carId) {
        gridPane.getChildren().remove(vehicles.get("car_" + carId).shape);
        vehicles.remove("car_" + carId);
    }

    @Override
    public double getCarProgress(int carId) {
        return Double.parseDouble( String.valueOf( vehicles.get("car_" + carId).pathTransition.getCurrentTime().toMillis() ) );

    }

    @Override
    public void setCarProgress(int carId, double duration) {
        if (duration < 0) {
            duration = duration + CARSPEED;
        }
        vehicles.get("car_" + carId).pathTransition.jumpTo(Duration.millis(duration));
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
            case 4: target = light_4; break;
            case 5: target = light_5; break;
            case 6: target = light_6; break;
            case 7: target = light_7; break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }

        target.setFill(fxColor);
    }


}

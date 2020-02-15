package sample;

import javafx.event.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class Controller {

    public Controller() {}

    @FXML
    private Path tramPath;

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
    private Path carPath;

    @FXML
    private MoveTo eastRoadBorder;

    @FXML
    private LineTo eastRoadLine;

    @FXML
    private MoveTo westRoadBorder;

    @FXML
    private LineTo westRoadLine;

    @FXML
    private Rectangle tram;

    @FXML
    private Rectangle AlphaCar;

    @FXML
    private Rectangle BetaCar;

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button resetButton;

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
    private void initialize() { }

    @FXML
    public void displayPos(MouseEvent event) {
        status.setText("X= " + status.getX() + " Y= " + status.getY());
    }


    @FXML
    public void setTramDynamic() {
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(10000));
        pathTransition.setNode(tram);
        pathTransition.setPath(tramPath);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);

        playButton.setOnAction(action -> {
            playButton.setDisable(true);
            pauseButton.setDisable(false);
            pathTransition.play();
        });

        pauseButton.setOnAction(action -> {
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            pathTransition.pause();
        });

    }

    @FXML
    public void playTram() {
        Timeline tl = new Timeline();

        tl.setCycleCount(2);
        tl.setAutoReverse(true);

        KeyValue kv = new KeyValue(tram.translateXProperty(), 200);
        KeyFrame kf = new KeyFrame(Duration.millis(7000), kv);
        tl.getKeyFrames().addAll(kf);

        tl.play();
    }

    public void stopTram() {
    }



}
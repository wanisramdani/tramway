package sample;

import com.sun.javaws.exceptions.MissingVersionResponseException;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.animation.Transition.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.MouseEvent;

public class MapView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
       
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        Controller ctrl = fxmlLoader.getController();
        ctrl.setTramDynamic();
        primaryStage.setScene(new Scene(root, 1266, 678));
        primaryStage.show();
    }

}

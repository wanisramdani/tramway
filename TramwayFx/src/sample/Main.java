package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;

import static java.lang.Thread.sleep;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("worldview.fxml"));
        Parent root = fxmlLoader.load();
        Controller ctrl = fxmlLoader.getController();

        ctrl.setLightColor(TrafficColor.GREEN, 7);
        ctrl.setTramDynamic();

        Scene scene = new Scene(root, 1266, 678);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TramwayFX");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

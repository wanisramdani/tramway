package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("worldview.fxml"));
        Parent root = fxmlLoader.load();
        WorldView worldView = fxmlLoader.getController();
        /*
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // WorldController.updateView();
            }
        };
        timer.start();
        */

        worldView.addEventListeners();
        Scene scene = new Scene(root, 1266, 678);
        scene.getStylesheets().add("sample/resources/WorldView.java.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("TramwayFX");
        primaryStage.show();

        // TESTING
        worldView.createTram(0);
        worldView.setTramDynamic(0, true);
        worldView.setTramDynamic(0, false);
        worldView.setTramProgress(0, worldView.TRAM_DELTA);

        worldView.createTram(1);
        worldView.setTramDynamic(1, true);
        worldView.setTramDynamic(1, false);
        worldView.setTramProgress(1, 0);

        worldView.createCar(0, TrafficDirection.NORTH);
        worldView.setCarDynamic(0, true);
        worldView.setCarDynamic(0, false);
        worldView.setCarProgress(0, 0);

        worldView.createCar(3, TrafficDirection.NORTH);
        worldView.setCarDynamic(3, true);
        worldView.setCarDynamic(3, false);
        worldView.setCarProgress(3, WorldView.CAR_DELTA);

        worldView.createCar(1, TrafficDirection.SOUTH);
        worldView.setCarDynamic(1, true);
        worldView.setCarDynamic(1, false);
        worldView.setCarProgress(1, 0);


        worldView.createCar(2, TrafficDirection.SOUTH);
        worldView.setCarDynamic(2, true);
        worldView.setCarDynamic(2, false);
        worldView.setCarProgress(2, WorldView.CAR_DELTA);


    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

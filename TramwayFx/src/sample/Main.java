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

        worldView.addEventListeners();
        Scene scene = new Scene(root, 1266, 678);
        scene.getStylesheets().add("sample/resources/WorldView.java.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("TramwayFX");
        primaryStage.show();

        //test(worldView);

        WorldModel worldModel = new WorldModel();
        WorldController worldController = new WorldController(worldModel);
        worldView.worldController = worldController;
        worldController.worldView = worldView;

        worldView.startAll();
        worldController.startAll();
        worldModel.startAll();

    }

    void test(WorldView worldView) {
        worldView.shouldLoop = true;

        worldView.createTram(0);
        worldView.setTramDynamic(0, true);
        worldView.setTramDynamic(0, false);
        worldView.setTramProgress(0, "segment_1_end");

        worldView.createTram(1);
        worldView.setTramDynamic(1, true);
        worldView.setTramDynamic(1, false);
        worldView.setTramProgress(1, "segment_0_end");

        worldView.createTram(2);
        worldView.setTramDynamic(2, true);
        worldView.setTramDynamic(2, false);
        worldView.setTramProgress(2, "segment_2_end");

        worldView.createTram(3);
        worldView.setTramDynamic(3, true);
        worldView.setTramDynamic(3, false);
        worldView.setTramProgress(3, -worldView.getDeltaConstant());

        worldView.createCar(0, TrafficDirection.NORTH);
        worldView.setCarDynamic(0, true);
        worldView.setCarDynamic(0, false);
        worldView.setCarProgress(0, 0);

        worldView.createCar(1, TrafficDirection.SOUTH);
        worldView.setCarDynamic(1, true);
        worldView.setCarDynamic(1, false);
        worldView.setCarProgress(1, 0);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

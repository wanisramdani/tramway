package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        Scene scene = new Scene(root, 1266, 678);
        worldView.setLightColor(0, TrafficColor.GREEN);
        worldView.startAnimate();

        primaryStage.setScene(scene);
        primaryStage.setTitle("TramwayFX");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

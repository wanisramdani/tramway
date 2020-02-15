package sample;

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
        Controller ctrl = fxmlLoader.getController();


        Scene scene = new Scene(root, 1266, 678);

        ctrl.setLightColor(TrafficColor.GREEN, 7);
        ctrl.setTramDynamic(0, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TramwayFX");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

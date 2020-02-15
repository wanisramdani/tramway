package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class WorldView extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("worldview.fxml"));
        Parent root = fxmlLoader.load();
        Controller ctrl = fxmlLoader.getController();
        ctrl.playTram();
        primaryStage.setScene(new Scene(root, 1266, 678));
        primaryStage.show();
    }

}

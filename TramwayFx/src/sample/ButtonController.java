package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class ButtonController {
    WorldView worldView;
    public ButtonController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("worldview.fxml"));
        worldView = fxmlLoader.getController();

    }

    private Button playButton;

    private Button pauseButton;

    private Button resetButton;


}

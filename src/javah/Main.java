package javah;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the javah window.
        FXMLLoader mainFxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/scene_main.fxml"));
        Scene mainScene = new Scene(mainFxmlLoader.load());

        // Initialize the primary stage containing the javah scene.
        primaryStage.setScene(mainScene);
        primaryStage.show();

        primaryStage.setMaximized(true);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
package javah;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Calendar;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Initialize the javah window.
        FXMLLoader mainFxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/scene_main.fxml"));
        Scene mainScene = new Scene(mainFxmlLoader.load());

        // Initialize the primary stage containing the javah scene.
        primaryStage.setScene(mainScene);
        primaryStage.show();

        System.out.println(Calendar.getInstance().get(Calendar.YEAR) - 2000);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
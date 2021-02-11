package artemis.Views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StudentDashboard extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("StudentDashboard.fxml"));
        Scene scene = new Scene(root,1000,650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

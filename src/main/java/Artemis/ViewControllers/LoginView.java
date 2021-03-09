package Artemis.ViewControllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends Application {




    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
        Scene scene = new Scene(root, 893,556);
        scene.getStylesheets().add("LoginViewStylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();







    }
}
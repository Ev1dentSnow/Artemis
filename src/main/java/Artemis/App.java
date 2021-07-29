/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Artemis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application{


    public static final String BASEURL = "http://127.0.0.1:8000/";



    public static void main(String[] args){


        launch(args);

    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        Parent root = loader.load();
        Scene loginScene = new Scene(root);
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/Images/ArtemisAlpha.png")));
        primaryStage.setTitle("Artemis");
        primaryStage.setScene(loginScene);
        primaryStage.show();

    }


}

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Artemis;

import Artemis.Controllers.LoginController;
import Artemis.Controllers.StudentDashboard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application{

    private String permissionLevel = "";
    private String accessToken = "";

    public static void main(String[] args){


        launch(args);

    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    loginController.start(primaryStage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });




        this.setAccessToken(loginController.getAccessToken());
        this.setPermissionLevel(loginController.getRole());
        System.out.println(loginController.getAccessToken());// line for testing purposes

        StudentDashboard studentDashboardController = (StudentDashboard) loader.getController();




    }








    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}

package Artemis.ViewControllers;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends Application {


    String[] userTypes = {"Student", "Teacher", "Administrator"};

    @FXML
    ComboBox choiceBox = new ComboBox(FXCollections.observableArrayList(userTypes));

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        AnchorPane root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
        root.getChildren().add(choiceBox);

        choiceBox.setLayoutX(520);
        choiceBox.setLayoutY(365);

        Scene scene = new Scene(root, 893,556);
        scene.getStylesheets().add("LoginViewStylesheet.css");

        primaryStage.setScene(scene);
        primaryStage.show();







    }
}
package artemis.Views;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class StudentDashboard extends Application {

    //StackPane config
    @FXML
    Pane stackPane = new StackPane();
    @FXML
    Pane homePane = new Pane();
    @FXML
    Pane marksPane = new Pane();
    @FXML
    Pane subjectsPane = new Pane();
    @FXML
    Pane disciplinePane = new Pane();
    @FXML
    Pane weatherPane = new Pane();

    Pane[] paneArr = {homePane,marksPane,subjectsPane,disciplinePane,weatherPane};
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        //Load FXML file and display window
        Parent root = FXMLLoader.load(getClass().getResource("/artemis/Views/StudentDashboard.fxml"));
        Scene scene = new Scene(root,1100,650);
        primaryStage.setScene(scene);
        primaryStage.show();
        this.hidePreviousPane(homePane);

    }

    @FXML
    public void btnHomeActionPerformed(javafx.event.ActionEvent event){
    hidePreviousPane(homePane);
    }

    @FXML
    public void btnMarksActionPerformed(javafx.event.ActionEvent event){
    hidePreviousPane(marksPane);

    }


    @FXML
    public void btnSubjectsActionPerformed(javafx.event.ActionEvent event){
    hidePreviousPane(subjectsPane);
    }

    @FXML
    public void btnDisciplineActionPerformed(javafx.event.ActionEvent event){
    hidePreviousPane(disciplinePane);
    }

    @FXML
    public void btnWeatherActionPerformed(javafx.event.ActionEvent event){
    hidePreviousPane(weatherPane);
    }

    @FXML
    public void btnExitActionPerformed(javafx.event.ActionEvent event){
        
    }

    private void hidePreviousPane(Pane currentPane){
        stackPane.getChildren().clear();
        stackPane.getChildren().add(currentPane);
    }



}

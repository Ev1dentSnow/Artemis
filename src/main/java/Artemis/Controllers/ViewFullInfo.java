package Artemis.Controllers;

import Artemis.Models.Student;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewFullInfo extends Application implements Initializable {

    @FXML
    private TextField txfFirstName;
    @FXML
    private TextField txfLastName;
    @FXML
    private TextField txfDOB;
    @FXML
    private TextField txfAge;
    @FXML
    private TextField txfEmail;
    @FXML
    private TextField txfHouse;
    @FXML
    private TextField txfForm;
    @FXML
    private TextField txfPrimaryContactName;
    @FXML
    private TextField txfPrimaryContactEmail;
    @FXML
    private TextField txfSecondaryContactName;
    @FXML
    private TextField txfSecondaryContactEmail;
    @FXML
    private TextArea txaComments;

    private Stage primaryStage;

    private static Student currentStudent;

    public static void main(String[] args) {
        launch(args);
    }

    private String accessToken = AdminDashboard.getAccessToken();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setTextFields(currentStudent);

    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {

    }

    private void setTextFields(Student student){

        txfFirstName.setText(student.getFirstName());
        txfLastName.setText(student.getLastName());
        txfDOB.setText(student.getDob().toString());
        txfAge.setText("");
        txfEmail.setText(student.getEmail());
        txfForm.setText("");
        txfPrimaryContactName.setText(student.getPrimaryContactName());
        txfPrimaryContactEmail.setText(student.getSecondaryContactEmail());
        txfSecondaryContactName.setText(student.getSecondaryContactName());
        txfSecondaryContactEmail.setText(student.getSecondaryContactEmail());
        txaComments.setText(student.getComments());

    }


    private void displayAlert(String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static void setCurrentStudent(Student currentStudent) {
        ViewFullInfo.currentStudent = currentStudent;
    }
}

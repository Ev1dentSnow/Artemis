package Artemis.Controllers;

import Artemis.Models.Student;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class StudentFullInfo extends Application implements Initializable {

    private boolean editModeEnabled = false;

    @FXML
    JFXToggleButton editModeSwitch = new JFXToggleButton();

    @FXML
    private TextField txfFirstName;
    @FXML
    private TextField txfLastName;
    @FXML
    private TextField txfUsername;
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
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;

    private Stage primaryStage;

    private static Student currentStudent;

    public static void main(String[] args) {
        launch(args);
    }

    private String accessToken = AdminDashboard.getAccessToken();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Exception handling for if currentStudent is null, as sometimes we want to open this window to add a new student,
        //and thus current student will be null. Thus we want to "ignore" the NullPointerException
        try {
            setTextFields(currentStudent);
        }
        catch(NullPointerException n){

        }

        //edit mode is disabled by default to prevent accidents
        disableTextFields();

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
        txfUsername.setText(student.getUsername());
        txfDOB.setText(student.getDob().toString());
        txfAge.setText(String.valueOf(calculateAge(student.getDob())));
        txfEmail.setText(student.getEmail());
        txfForm.setText(String.valueOf(student.getForm()));
        txfPrimaryContactName.setText(student.getPrimaryContactName());
        txfPrimaryContactEmail.setText(student.getSecondaryContactEmail());
        txfSecondaryContactName.setText(student.getSecondaryContactName());
        txfSecondaryContactEmail.setText(student.getSecondaryContactEmail());
        txaComments.setText(student.getComments());

    }

    private void enableTextFields(){
        txfFirstName.setDisable(false);
        txfLastName.setDisable(false);
        txfUsername.setDisable(false);
        txfDOB.setDisable(false);
        txfAge.setDisable(false);
        txfEmail.setDisable(false);
        txfForm.setDisable(false);
        txfHouse.setDisable(false);
        txfPrimaryContactName.setDisable(false);
        txfPrimaryContactEmail.setDisable(false);
        txfSecondaryContactName.setDisable(false);
        txfSecondaryContactEmail.setDisable(false);
        txaComments.setDisable(false);
    }

    private void disableTextFields(){
        txfFirstName.setDisable(true);
        txfLastName.setDisable(true);
        txfUsername.setDisable(true);
        txfDOB.setDisable(true);
        txfAge.setDisable(true);
        txfEmail.setDisable(true);
        txfForm.setDisable(true);
        txfHouse.setDisable(true);
        txfPrimaryContactName.setDisable(true);
        txfPrimaryContactEmail.setDisable(true);
        txfSecondaryContactName.setDisable(true);
        txfSecondaryContactEmail.setDisable(true);
        txaComments.setDisable(true);
    }

    private int calculateAge(Date dateOfBirth){
        Period difference = Period.between(dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now());
        return difference.getYears();
    }


    private void displayAlert(String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void btnConfirmActionPerformed(ActionEvent event){

        event.consume();
        try {
            performHTTP_PUT("https://artemisystem.xyz/api/student/" + currentStudent.getId());
        }
        catch(IOException e){
            displayAlert("An error occured while sending data to the server", Alert.AlertType.ERROR);
        }

        Stage currentWindow = (Stage) btnConfirm.getScene().getWindow();
        currentWindow.close();
    }

    @FXML
    private void btnCancelActionPerformed(ActionEvent event){
        event.consume();
        Stage currentWindow = (Stage) btnConfirm.getScene().getWindow();
        currentWindow.close();
    }

    @FXML
    private void editModeSwitchActionPerformed(ActionEvent event){
        event.consume();

        if (!editModeEnabled){
            enableTextFields();
            editModeEnabled = true;
        }
        else{
            disableTextFields();
            editModeEnabled = false;
        }

        System.out.println("WAHAHAHAHAHAHAHAHA");
    }

    private String performHTTP_PUT(String url) throws IOException {

        //TODO: Improve http request status code handling

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut request = new HttpPut(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair("first_name", txfFirstName.getText()));
        body.add(new BasicNameValuePair("last_name", txfLastName.getText()));
        body.add(new BasicNameValuePair("house", txfHouse.getText()));
        body.add(new BasicNameValuePair("email", txfEmail.getText()));
        body.add(new BasicNameValuePair("dob", txfDOB.getText()));
        body.add(new BasicNameValuePair("primary_contact_email", txfPrimaryContactEmail.getText()));
        body.add(new BasicNameValuePair("primary_contact_name", txfPrimaryContactName.getText()));
        body.add(new BasicNameValuePair("secondary_contact_email", txfSecondaryContactEmail.getText()));
        body.add(new BasicNameValuePair("secondary_contact_name", txfSecondaryContactName.getText()));
        body.add(new BasicNameValuePair("comments", txaComments.getText()));

        request.setEntity(new UrlEncodedFormEntity(body));
        Alert alert = new Alert(Alert.AlertType.ERROR);

        try{
            CloseableHttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
        catch (ConnectException e){
            alert.setContentText("Error connecting to server");
            alert.showAndWait();
        }
        return null;
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static void setCurrentStudent(Student currentStudent) {
        StudentFullInfo.currentStudent = currentStudent;
    }
}

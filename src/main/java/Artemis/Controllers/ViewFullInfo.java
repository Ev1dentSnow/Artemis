package Artemis.Controllers;

import Artemis.Models.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
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

    private static int selectedStudentId;

    public static void main(String[] args) {
        launch(args);
    }

    private String accessToken = AdminDashboard.getAccessToken();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Student currentStudent = null;

        try {
            CloseableHttpResponse response = performHttpGet("https://artemisystem.xyz/api/student/" + selectedStudentId);
            if(response.getStatusLine().getStatusCode() == 200){
                Gson gson = new GsonBuilder()
                        .setDateFormat("EEE, DD MMMM yyyy HH:mm:ss z").create();

                currentStudent = gson.fromJson(EntityUtils.toString(response.getEntity()), Student.class);
            }
            else if(response.getStatusLine().getStatusCode() == 401){
                displayAlert("Unauthorized, please reauthenticate", Alert.AlertType.ERROR);
            }
            else if(response.getStatusLine().getStatusCode() == 403){
                displayAlert("Forbidden, please reauthenticate", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTextFields(currentStudent);



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

    private CloseableHttpResponse performHttpGet(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try{
            CloseableHttpResponse response = client.execute(request);
            return response;
        }

        catch(ConnectException e){
            alert.setContentText("Error connecting to server");
            alert.showAndWait();
        }

        return null;
    }

    private void displayAlert(String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static int getSelectedStudentId() {
        return selectedStudentId;
    }

    public static void setSelectedStudentId(int selectedStudentId) {
        ViewFullInfo.selectedStudentId = selectedStudentId;
    }
}

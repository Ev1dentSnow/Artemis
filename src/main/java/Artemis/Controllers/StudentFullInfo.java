package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.JSON.Serializers.StudentJSON;
import Artemis.Models.Student;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

public class StudentFullInfo extends Application implements Initializable {

    private boolean editModeEnabled = false;




    /** This boolean is used to determine whether this window is being used for a POST or a PATCH request, as otherwise
     *  the "confirm" button would not know which type of HTTP Request to execute. If false, it
     *  is being used for a PATCH request
     */
    public static boolean postRequest = false;

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
    @FXML
    private Button btnResetPassword;

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

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        txfFirstName.setText(student.getFirstName());
        txfLastName.setText(student.getLastName());
        txfUsername.setText(student.getUsername());
        txfDOB.setText(formatter.format(student.getDob()));
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


    private Optional<ButtonType> displayAlert(String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        if (alertType == Alert.AlertType.CONFIRMATION){
            Optional<ButtonType> result = alert.showAndWait();
            return result;
        }
        else{
            alert.show();
        }
        return null;
    }

    @FXML
    private void btnConfirmActionPerformed(ActionEvent event) throws IOException {

        //TODO: Optimise the poo out of this method

        event.consume();

        String firstName = txfFirstName.getText();
        String lastName = txfLastName.getText();
        String username = txfUsername.getText();
        //TODO: Add "date" data validation for txfDOB, and data all fields
        String dob = txfDOB.getText();
        String age = txfAge.getText();
        String email = txfEmail.getText();
        String house = txfHouse.getText();
        String comments = txaComments.getText();
        int form = Integer.parseInt(txfForm.getText());
        String primaryContactName = txfPrimaryContactName.getText();
        String primaryContactEmail = txfPrimaryContactEmail.getText();
        String secondaryContactName = txfSecondaryContactName.getText();
        String secondaryContactEmail = txfSecondaryContactEmail.getText();

        int enrollmentYear = Calendar.getInstance().get(Calendar.YEAR);


        if (postRequest) {

            HashMap<String, String> userDetails = new HashMap<String, String>();


            userDetails.put("first_name", firstName);
            userDetails.put("last_name", lastName);
            userDetails.put("username", username);
            userDetails.put("password", username); //Default password is the same as the student's username
            userDetails.put("dob", dob);
            userDetails.put("email", email);
            userDetails.put("house", house);

            if(txaComments.getText().equals("")){
                userDetails.remove("comments");
            }

            StudentJSON student = new StudentJSON(userDetails, form, enrollmentYear, primaryContactName, primaryContactEmail, secondaryContactName, secondaryContactEmail);

            Gson gson = new Gson();
            String json = gson.toJson(student);

            try {
                String result = performHTTP_POST(App.BASEURL + App.STUDENT_LIST_PATH, json);
                System.out.println(result);
            } catch (IOException e) {
                displayAlert("An error occured while sending data to the server", Alert.AlertType.ERROR);
            }
        }
        else{

            HashMap<String, String> userDetails = new HashMap<String, String>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            userDetails.put("first_name", firstName);
            userDetails.put("last_name", lastName);
            userDetails.put("username", username);
            userDetails.put("password", username); //Default password is the same as the student's username
            userDetails.put("dob", dob);
            userDetails.put("email", email);
            userDetails.put("house", house);
            userDetails.put("comments", comments);

            Iterator userDetailsIterator = userDetails.entrySet().iterator();

            while(userDetailsIterator.hasNext()){
                Map.Entry userDetailsElement = (Map.Entry) userDetailsIterator.next();
                String value = (String) userDetailsElement.getValue();

                if(value.equals("")){
                    userDetailsIterator.remove();
                }
            }

            StudentJSON student = new StudentJSON(userDetails, form, enrollmentYear, primaryContactName, primaryContactEmail, secondaryContactName, secondaryContactEmail);

            Gson gson = new Gson();
            String json = gson.toJson(student);

            try {
               CloseableHttpResponse wa = performHTTP_PATCH(App.BASEURL + App.STUDENT_LIST_PATH + currentStudent.getId(), json);
                System.out.println(EntityUtils.toString(wa.getEntity()));
            }
            catch(IOException e) {
                displayAlert("An error occured while sending data to the server", Alert.AlertType.ERROR);
            }
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

    }

    @FXML
    private void resetPasswordActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        Optional result = displayAlert("Warning! Only do this if this user does not have access to the email they registered " +
        "with. Doing this will set this user's password to the same as their username. Are you sure you wish to continue?",
                Alert.AlertType.CONFIRMATION);

        if (result.get() == ButtonType.OK){
            String json = String.format("{\"user_details\": {\"password\": \"%s\"}}", currentStudent.getUsername());
            performHTTP_PATCH(App.BASEURL + App.STUDENT_LIST_PATH + currentStudent.getId(), json);
        }

    }

    private String performHTTP_POST(String url, String jsonBody) throws IOException {

        //TODO: Improve http request status code handling

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        StringEntity entity = new StringEntity(jsonBody);
        request.setEntity(entity);

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

    private CloseableHttpResponse performHTTP_PATCH(String url, String jsonBody) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPatch request = new HttpPatch(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        StringEntity entity = new StringEntity(jsonBody);
        request.setEntity(entity);

        Alert alert = new Alert(Alert.AlertType.ERROR);

        try {
            CloseableHttpResponse response = client.execute(request);
            return response;
        }
        catch(ConnectException e){
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

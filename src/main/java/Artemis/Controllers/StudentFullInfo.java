package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.House;
import Artemis.Models.JSON.Serializers.StudentJSON;
import Artemis.Models.Student;
import Artemis.Models.User;
import com.google.gson.Gson;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

public class StudentFullInfo extends Application implements Initializable {

    private boolean editModeEnabled = false;




    /** This boolean is used to determine whether this window is being used for a POST or a PATCH request, as otherwise
     *  the "confirm" button would not know which type of HTTP Request to execute. If false, it
     *  is being used for a PATCH request
     */
    public static boolean postRequest = false;

    CloseableHttpClient client = HttpClients.createDefault();
    private final String USERS_PATH = "api/users";

    @FXML
    Label lblFirstNameError;
    @FXML
    Label lblLastNameError;
    @FXML
    Label lblUsernameError;
    @FXML
    Label lblDobError;
    @FXML
    Label lblFormError;
    @FXML
    Label lblEmailError;
    @FXML
    Label lblPrimaryContactNameError;
    @FXML
    Label lblPrimaryContactEmailError;
    @FXML
    Label lblSecondaryContactNameError;
    @FXML
    Label lblSecondaryContactEmailError;


    @FXML
    MFXToggleButton editModeSwitch = new MFXToggleButton();

    @FXML
    private TextField txfFirstName;
    @FXML
    private TextField txfLastName;
    @FXML
    private TextField txfUsername;
    @FXML
    private TextField txfDOB;
    @FXML
    private TextField txfNewPassword;
    @FXML
    private TextField txfEmail;
    @FXML
    private MFXComboBox<String> houseComboBox;
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
    //used to see which email addresses and usernames have already been taken
    private User[] usersList;

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

        houseComboBox.getItems().add(new House("Ochse", "Blue").getName());
        houseComboBox.getItems().add(new House("MacRobert", "Red").getName());
        houseComboBox.getItems().add(new House("Knoll", "Black").getName());
        houseComboBox.getItems().add(new House("DeBeer", "Yellow").getName());
        houseComboBox.getItems().add(new House("Knapp Fischer", "Green").getName());
        houseComboBox.getItems().add(new House("Murray", "Purple").getName());

        //If it's a POST request (new student being created), there's no such thing as a current student
        if(!postRequest){
            houseComboBox.getSelectionModel().selectItem(currentStudent.getHouse());
        }



        //edit mode is disabled by default to prevent accidents
        disableTextFields();
        resetErrorLabels();
        try {
            getReservedEmailsAndUsernamesList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {

    }

    /**
     * Gets a list of existing usernames and emails to ensure that no duplicate usernames or email addresses
     * are entered into the system
     * @throws IOException
     */

    private void getReservedEmailsAndUsernamesList() throws IOException {
        CloseableHttpResponse response = performHttpGet(App.BASEURL + USERS_PATH);
        String json = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        usersList = gson.fromJson(json, User[].class);
    }

    private CloseableHttpResponse performHttpGet(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        try{
            CloseableHttpResponse response = client.execute(request);
            return response;
        }
        catch(ConnectException e){
            displayAlert("Error connecting to server", Alert.AlertType.ERROR);
        }

        return null;
    }


    private void setTextFields(Student student){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        txfFirstName.setText(student.getFirstName());
        txfLastName.setText(student.getLastName());
        txfUsername.setText(student.getUsername());
        txfDOB.setText(formatter.format(student.getDob()));
        txfNewPassword.setText("");
        txfEmail.setText(student.getEmail());
        txfForm.setText(String.valueOf(student.getForm()));
        txfPrimaryContactName.setText(student.getPrimaryContactName());
        txfPrimaryContactEmail.setText(student.getPrimaryContactEmail());
        txfSecondaryContactName.setText(student.getSecondaryContactName());
        txfSecondaryContactEmail.setText(student.getSecondaryContactEmail());
        txaComments.setText(student.getComments());

    }

    private void enableTextFields(){
        txfFirstName.setDisable(false);
        txfLastName.setDisable(false);
        txfUsername.setDisable(false);
        txfDOB.setDisable(false);
        txfNewPassword.setDisable(false);
        txfEmail.setDisable(false);
        txfForm.setDisable(false);
        txfPrimaryContactName.setDisable(false);
        txfPrimaryContactEmail.setDisable(false);
        txfSecondaryContactName.setDisable(false);
        txfSecondaryContactEmail.setDisable(false);
        txaComments.setDisable(false);
        houseComboBox.setDisable(false);
    }

    private void disableTextFields(){
        txfFirstName.setDisable(true);
        txfLastName.setDisable(true);
        txfUsername.setDisable(true);
        txfDOB.setDisable(true);
        txfNewPassword.setDisable(true);
        txfEmail.setDisable(true);
        txfForm.setDisable(true);
        txfPrimaryContactName.setDisable(true);
        txfPrimaryContactEmail.setDisable(true);
        txfSecondaryContactName.setDisable(true);
        txfSecondaryContactEmail.setDisable(true);
        txaComments.setDisable(true);
        houseComboBox.setDisable(true);
    }

    private void resetErrorLabels(){
        lblFirstNameError.setStyle("-fx-text-fill: #202020;");
        lblLastNameError.setStyle("-fx-text-fill: #202020;");
        lblUsernameError.setStyle("-fx-text-fill: #202020;");
        lblEmailError.setStyle("-fx-text-fill: #202020;");
        lblDobError.setStyle("-fx-text-fill: #202020;");
        lblFormError.setStyle("-fx-text-fill: #202020;");
        lblPrimaryContactNameError.setStyle("-fx-text-fill: #202020;");
        lblPrimaryContactEmailError.setStyle("-fx-text-fill: #202020;");
        lblSecondaryContactNameError.setStyle("-fx-text-fill: #202020;");
        lblSecondaryContactEmailError.setStyle("-fx-text-fill: #202020;");
    }

    private void triggerErrorLabel(Label errorLabel){
        errorLabel.setStyle("-fx-text-fill: red;");
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
    private void btnConfirmActionPerformed(ActionEvent event) throws IOException, ParseException {

        //TODO: Optimise the poo out of this method

        event.consume();
        resetErrorLabels();

        String firstName = txfFirstName.getText();
        String lastName = txfLastName.getText();
        String username = txfUsername.getText();
        //TODO: Add "date" data validation for txfDOB, and data all fields
        String dob = txfDOB.getText();
        String newPassword = txfNewPassword.getText();
        String email = txfEmail.getText();
        String house = houseComboBox.getSelectionModel().getSelectedItem();
        String comments = txaComments.getText();
        String form = txfForm.getText();
        String primaryContactName = txfPrimaryContactName.getText();
        String primaryContactEmail = txfPrimaryContactEmail.getText();
        String secondaryContactName = txfSecondaryContactName.getText();
        String secondaryContactEmail = txfSecondaryContactEmail.getText();
        int enrollmentYear = Calendar.getInstance().get(Calendar.YEAR);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


        ArrayList<TextField> invalidFields = validateData(firstName, lastName, username, dob, email, form,
                                                          primaryContactName, primaryContactEmail, secondaryContactName,
                                                          secondaryContactEmail);

        boolean noInvalidFields = true;

        if (invalidFields.contains(txfFirstName)){
            triggerErrorLabel(lblFirstNameError);
            noInvalidFields = false;
        }
        if (invalidFields.contains(txfLastName)){
            triggerErrorLabel(lblLastNameError);
            noInvalidFields = false;
        }
        if (invalidFields.contains(txfUsername)){
            triggerErrorLabel(lblUsernameError);
            noInvalidFields = false;
        }
        if (invalidFields.contains(txfDOB)){
            triggerErrorLabel(lblDobError);
            noInvalidFields = false;
        }
        if (invalidFields.contains(txfEmail)){
            triggerErrorLabel(lblEmailError);
            noInvalidFields = false;
        }
        if (invalidFields.contains(txfForm)){
            triggerErrorLabel(lblFormError);
            noInvalidFields = false;
        }
        if (invalidFields.contains(txfPrimaryContactName)){
            triggerErrorLabel(lblPrimaryContactNameError);
            noInvalidFields = false;
        }
        if(invalidFields.contains(txfPrimaryContactEmail)){
            triggerErrorLabel(lblPrimaryContactEmailError);
            noInvalidFields = false;
        }
        if(invalidFields.contains(txfSecondaryContactName)){
            triggerErrorLabel(lblSecondaryContactNameError);
            noInvalidFields = false;
        }
        if(invalidFields.contains(txfSecondaryContactEmail)){
            triggerErrorLabel(lblSecondaryContactEmailError);
            noInvalidFields = false;
        }
        if(houseComboBox.getSelectionModel().getSelectedItem() == null){
            noInvalidFields = false;
            displayAlert("A house must be selected", Alert.AlertType.ERROR);
        }

        if (noInvalidFields) {

            if (postRequest) {

                HashMap<String, String> userDetails = new HashMap<String, String>();
                userDetails.put("first_name", firstName);
                userDetails.put("last_name", lastName);
                userDetails.put("username", username);

                if(txfNewPassword.getText().equals("")) {
                    userDetails.put("password", username); //Default password is the same as the student's username if none is supplied
                }
                else{
                    userDetails.put("password", newPassword);
                }

                userDetails.put("dob", dob);
                userDetails.put("email", email);
                userDetails.put("house", house);

                if (txaComments.getText().equals("")) {
                    userDetails.remove("comments");
                }

                StudentJSON student = new StudentJSON(userDetails, Integer.parseInt(form), enrollmentYear, primaryContactName, primaryContactEmail, secondaryContactName, secondaryContactEmail);
                Gson gson = new Gson();
                String json = gson.toJson(student);

                try {
                    String result = performHTTP_POST(App.BASEURL + App.STUDENT_LIST_PATH, json);
                    System.out.println(result);
                } catch (IOException e) {
                    displayAlert("An error occured while sending data to the server", Alert.AlertType.ERROR);
                }
            }
            else {

                HashMap<String, String> userDetails = new HashMap<String, String>();

                userDetails.put("first_name", firstName);
                userDetails.put("last_name", lastName);
                userDetails.put("username", username);

                if(!txfNewPassword.getText().equals("")) {
                    userDetails.put("password", newPassword);
                }

                userDetails.put("dob", dob);
                userDetails.put("email", email);
                userDetails.put("house", house);
                userDetails.put("comments", comments);

                Iterator userDetailsIterator = userDetails.entrySet().iterator();

                while (userDetailsIterator.hasNext()) {
                    Map.Entry userDetailsElement = (Map.Entry) userDetailsIterator.next();
                    String value = (String) userDetailsElement.getValue();

                    if (value.equals("")) {
                        userDetailsIterator.remove();
                    }
                }

                StudentJSON student = new StudentJSON(userDetails, Integer.parseInt(form), enrollmentYear, primaryContactName, primaryContactEmail, secondaryContactName, secondaryContactEmail);

                Gson gson = new Gson();
                String json = gson.toJson(student);

                try {
                    CloseableHttpResponse wa = performHTTP_PATCH(App.BASEURL + App.STUDENT_LIST_PATH + currentStudent.getId(), json);
                    System.out.println(EntityUtils.toString(wa.getEntity()));
                } catch (IOException e) {
                    displayAlert("An error occured while sending data to the server", Alert.AlertType.ERROR);
                }
            }

            Stage currentWindow = (Stage) btnConfirm.getScene().getWindow();
            currentWindow.close();

        }
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
        Stage currentWindow = (Stage) lblEmailError.getScene().getWindow();
        currentWindow.close();

    }

    /**
     * Validates that all data entered is in the correct format as defined by the REST API
     * @param firstName
     * @param lastName
     * @param username
     * @param dob
     * @param email
     * @param form
     * @param primaryContactName
     * @param primaryContactEmail
     * @param secondaryContactName
     * @param secondaryContactEmail
     * @return Arraylist of invalid TextFields
     */

    private ArrayList<TextField> validateData(String firstName, String lastName, String username, String dob, String email,
                                              String form, String primaryContactName, String primaryContactEmail,
                                              String secondaryContactName, String secondaryContactEmail){


        ArrayList<TextField> invalidFields = new ArrayList<>();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        //Generic text user info validation
        if(firstName == null || firstName.equals("")){
            invalidFields.add(txfFirstName);
        }
        if(lastName == null || lastName.equals("")){
            invalidFields.add(txfLastName);
        }
        if(username == null || username.equals("")){
            invalidFields.add(txfUsername);
        }

        //Check if username is already taken
        for(User user : usersList){
            if(user.getUsername().equals(username)){
                invalidFields.add(txfUsername);
                displayAlert("This username has already been taken", Alert.AlertType.ERROR);
            }
        }

        if(primaryContactName == null || primaryContactName.equals("")){
            invalidFields.add(txfPrimaryContactName);
        }
        if(secondaryContactName == null || secondaryContactName.equals("")){
            invalidFields.add(txfSecondaryContactName);
        }


        //DOB Validation
        try{
            dateFormatter.parse(dob);
        }
        catch(ParseException p){
            invalidFields.add(txfDOB);
        }

        //Form Validation
        try{
            Integer.parseInt(form);
        }
        catch(NumberFormatException n){
            invalidFields.add(txfForm);
        }

        //Email Validation
        if(email == null || email.equals("") || !pattern.matcher(email).matches()){
            invalidFields.add(txfEmail);
        }

        //Check if student email has already been taken
        for(User user : usersList){
            if(user.getEmail().equals(email)){
                invalidFields.add(txfEmail);
                displayAlert("This student email address has already been taken", Alert.AlertType.ERROR);
            }
        }

        if(primaryContactEmail == null || primaryContactEmail.equals("") || !pattern.matcher(primaryContactEmail).matches()){
            invalidFields.add(txfPrimaryContactEmail);
        }
        if(secondaryContactEmail == null || secondaryContactEmail.equals("") || !pattern.matcher(secondaryContactEmail).matches()){
            invalidFields.add(txfSecondaryContactEmail);
        }
        return invalidFields;
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

    public boolean isEditModeEnabled() {
        return editModeEnabled;
    }

    public void setEditModeEnabled(boolean editModeEnabled) {
        this.editModeEnabled = editModeEnabled;
    }

}

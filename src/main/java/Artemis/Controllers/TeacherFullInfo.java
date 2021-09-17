package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.House;
import Artemis.Models.Student;
import Artemis.Models.Teacher;
import Artemis.Models.User;
import com.google.gson.Gson;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class TeacherFullInfo extends Application implements Initializable {

    /** This boolean is used to determine whether this window is being used for a POST or a PATCH request, as otherwise
     *  the "confirm" button would not know which type of HTTP Request to execute. If false, it
     *  is being used for a PATCH request
     */
    public static boolean postRequest = false;
    private boolean editModeEnabled = false;
    CloseableHttpClient client = HttpClients.createDefault();
    private final String USERS_PATH = "api/users";
    private static Student currentStudent;

    @FXML
    private Label lblFirstNameError;
    @FXML
    private Label lblLastNameError;
    @FXML
    private Label lblUsernameError;
    @FXML
    private Label lblDobError;
    @FXML
    private Label lblEmailError;
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
    private MFXComboBox<String> subjectComboBox;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;

    private String accessToken = AdminDashboard.getAccessToken();
    private static Teacher currentTeacher;
    private User[] usersList;


    @Override
    public void initialize(URL location, ResourceBundle resources){

        try {
            setTextFields(currentTeacher);
        }
        catch(NullPointerException n) {

        }

        houseComboBox.getItems().add(new House("Ochse", "Blue").getName());
        houseComboBox.getItems().add(new House("MacRobert", "Red").getName());
        houseComboBox.getItems().add(new House("Knoll", "Black").getName());
        houseComboBox.getItems().add(new House("DeBeer", "Yellow").getName());
        houseComboBox.getItems().add(new House("Knapp Fischer", "Green").getName());
        houseComboBox.getItems().add(new House("Murray", "Purple").getName());

        subjectComboBox.getItems().add("English");
        subjectComboBox.getItems().add("Mathematics");
        subjectComboBox.getItems().add("Physical Science");
        subjectComboBox.getItems().add("Business Studies");
        subjectComboBox.getItems().add("Information Technology");

        if(!postRequest){
            houseComboBox.getSelectionModel().selectItem(currentTeacher.getHouse());
            subjectComboBox.getSelectionModel().selectItem(currentTeacher.getSubject());
        }

        disableTextFields();
        resetErrorLabels();

        try {
            getReservedEmailsAndUsernamesList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

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

    @FXML
    private void btnConfirmActionPerformed(ActionEvent event){
        event.consume();

        if (postRequest) {

        }

        else {

        }

    }

    @FXML
    private void btnCancelActionPerformed(ActionEvent event){
        event.consume();
    }

    @FXML
    private void resetPasswordActionPerformed(ActionEvent event){
        event.consume();
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


    private void resetErrorLabels(){
        lblFirstNameError.setStyle("-fx-text-fill: #202020;");
        lblLastNameError.setStyle("-fx-text-fill: #202020;");
        lblUsernameError.setStyle("-fx-text-fill: #202020;");
        lblEmailError.setStyle("-fx-text-fill: #202020;");
        lblDobError.setStyle("-fx-text-fill: #202020;");
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


    private void setTextFields(Teacher teacher){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        txfFirstName.setText(teacher.getFirstName());
        txfLastName.setText(teacher.getLastName());
        txfUsername.setText(teacher.getUsername());
        txfDOB.setText(formatter.format(teacher.getDob()));
        txfNewPassword.setText("");
        txfEmail.setText(teacher.getEmail());

    }

    private void enableTextFields(){
        txfFirstName.setDisable(false);
        txfLastName.setDisable(false);
        txfUsername.setDisable(false);
        txfDOB.setDisable(false);
        txfNewPassword.setDisable(false);
        txfEmail.setDisable(false);
        houseComboBox.setDisable(false);
        subjectComboBox.setDisable(false);
    }

    private void disableTextFields(){
        txfFirstName.setDisable(true);
        txfLastName.setDisable(true);
        txfUsername.setDisable(true);
        txfDOB.setDisable(true);
        txfNewPassword.setDisable(true);
        txfEmail.setDisable(true);
        houseComboBox.setDisable(true);
        subjectComboBox.setDisable(true);
    }


    public static Teacher getCurrentTeacher() {
        return currentTeacher;
    }

    public static void setCurrentTeacher(Teacher currentTeacher) {
        TeacherFullInfo.currentTeacher = currentTeacher;
    }
}

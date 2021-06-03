package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.Student;
import com.calendarfx.view.YearMonthView;
import com.google.gson.Gson;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.collections.FXCollections.observableArrayList;

public class AdminDashboard extends Application implements Initializable {


    private static String accessToken;

    private static int userId;

    private boolean studentsPanePrepared = false;

    @FXML
    YearMonthView calendar = new YearMonthView();
    @FXML
    Label timeLabel = new Label();
    @FXML
    Label dateLabel = new Label();

    @FXML
    StackPane stackPane = new StackPane();
    @FXML
    Pane homePane = new Pane();
    @FXML
    Pane studentsPane = new Pane();
    @FXML
    Pane teachersPane = new Pane();
    @FXML
    Pane timetablesPane = new Pane();
    @FXML
    Button signOut = new Button();

    @FXML
    TableView studentsTable = new TableView();
    @FXML
    TableColumn<Student, Integer> colID;
    @FXML
    TableColumn<Student, String> colFirstName;
    @FXML
    TableColumn<Student, String> colLastName;
    @FXML
    TableColumn<Student, Integer> colForm;
    @FXML
    TableColumn<Student, String> colHouse;
    @FXML
    TableColumn<Student, String> colEmail;

    final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

    private ObservableList<Student> studentsList;

    private static Student selectedStudent;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //---------------------UPDATING DASHBOARD TIME EVERY SECOND ON ANOTHER THREAD-------------------------

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler() {
            //event.consume() is never called, so the event keeps looping over and over every second
            @Override
            public void handle(Event event) {
                //set timeLabel
                Calendar cal = Calendar.getInstance();
                timeLabel.setText(timeFormat.format(cal.getTime()));
                LocalDate date = LocalDate.now();
                //set dateLabel
                int day = fetchDayOfWeek(date);
                String dayString = fetchDayOfWeekString(date);
                dateLabel.setText(dayString + " " + day + " " + date.getMonth());

            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        //-------------------------------------------------------------------------------------------

        stackPane.getChildren().clear();
        stackPane.getChildren().add(homePane);


    }


    private CloseableHttpResponse performHttpPost(String url) throws  IOException{

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try{
            CloseableHttpResponse response = client.execute(request);
            return response;
        }
        catch (ConnectException e){
            alert.setContentText("Error connecting to server");
            alert.showAndWait();
        }
        return null;
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


    private int fetchDayOfWeek(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    private String fetchDayOfWeekString(LocalDate date){
        DayOfWeek day = date.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }



    @FXML
    private void homeActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(homePane);
    }

    @FXML
    private void studentsActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(studentsPane);

        if(!studentsPanePrepared){

            prepareStudentsPane();
            studentsPanePrepared = true;
        }

    }

    @FXML
    private void teachersActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(teachersPane);
    }

    @FXML
    private void timetablesActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(timetablesPane);
    }

    @FXML
    private void signOutActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        CloseableHttpResponse result = performHttpPost("https://artemisystem.xyz/api/auth/logout");
        if(result.getStatusLine().getStatusCode() == 200){
            displayAlert("Logged out successfully!", Alert.AlertType.INFORMATION);
            Stage stage = (Stage) signOut.getScene().getWindow();
            stage.close();
            AnchorPane LoginController = (AnchorPane) FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage window = new Stage();
            window.getIcons().add(new Image(App.class.getResourceAsStream("/fxmlAssets/ArtemisAlpha.png")));
            window.setTitle("Artemis");
            window.setScene(new Scene(LoginController));
            window.setResizable(false);
            window.show();
        }
        else{
            displayAlert(result.getEntity().getContent().toString(), Alert.AlertType.ERROR);
        }
    }

    private void prepareStudentsPane() throws IOException {
        CloseableHttpResponse response = performHttpGet("https://artemisystem.xyz/api/students");

        if(response.getStatusLine().getStatusCode() == 200){
            Gson gson = new Gson();

            studentsList = observableArrayList();

            studentsList.addAll(gson.fromJson(EntityUtils.toString(response.getEntity()), Student[].class));



            colID.setCellValueFactory(new PropertyValueFactory<>("id"));
            colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            colForm.setCellValueFactory(new PropertyValueFactory<>("Form"));
            colHouse.setCellValueFactory(new PropertyValueFactory<>("House"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
            studentsTable.setItems(studentsList);



        }
        else if(response.getStatusLine().getStatusCode() == 401){
            displayAlert("Unauthorized, please re-authenticate", Alert.AlertType.ERROR);
        }
        else if(response.getStatusLine().getStatusCode() == 403){
            displayAlert("Forbidden, please re-authenticate", Alert.AlertType.ERROR);
        }
    }


    private void displayAlert(String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void viewFullInfoActionPerformed(ActionEvent event) throws IOException {

        selectedStudent = (Student) studentsTable.getSelectionModel().getSelectedItem();



        if (!(selectedStudent == null)){
            ViewFullInfo.setSelectedStudentId(selectedStudent.getId());
            AnchorPane fullInfoPane = FXMLLoader.load(getClass().getResource("/StudentFullInfo.fxml"));
            Stage viewFullInfoStage = new Stage();
            Scene viewFullInfoScene = new Scene(fullInfoPane);
            viewFullInfoStage.setScene(viewFullInfoScene);
            viewFullInfoStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            viewFullInfoStage.initModality(Modality.APPLICATION_MODAL);
            viewFullInfoStage.setTitle("Full Information");
            viewFullInfoStage.show();
        }
        else{
            displayAlert("Select a student first", Alert.AlertType.ERROR);
        }

    }


    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        AdminDashboard.accessToken = accessToken;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        AdminDashboard.userId = userId;
    }

    public ObservableList<Student> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(ObservableList<Student> studentsList) {
        this.studentsList = studentsList;
    }
}

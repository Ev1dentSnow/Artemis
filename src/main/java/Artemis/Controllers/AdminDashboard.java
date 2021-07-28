package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.Announcement;
import Artemis.Models.JSON.Deserializers.StudentJSON;
import Artemis.Models.Student;
import Artemis.Models.Weather.Daily;
import Artemis.Models.Weather.ForecastWeather;
import Artemis.Models.Weather.Weather;
import com.calendarfx.view.YearMonthView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.image.ImageView;
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
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

public class AdminDashboard extends Application implements Initializable {


    private static String accessToken;

    private static int userId;

    private boolean studentsPanePrepared = false;

    private final String ANNOUCEMENTS_PATH = "api/announcements";
    private final String WEATHER_PATH = "api/weather";
    private final String STUDENT_LIST_PATH = "api/students";

    @FXML
    private ListView announcementList;

    //Weather "widget" images

    @FXML
    ImageView todayImage = new ImageView();
    @FXML
    ImageView secondDayImage = new ImageView();
    @FXML
    ImageView thirdDayImage = new ImageView();
    @FXML
    ImageView fourthDayImage = new ImageView();
    @FXML
    ImageView fifthDayImage = new ImageView();
    @FXML
    ImageView sixthDayImage = new ImageView();
    @FXML
    ImageView seventhDayImage = new ImageView();


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

    //Students Pane Componenets

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

    @FXML
    JFXButton btnAddStudent = new JFXButton();
    @FXML
    JFXButton btnRemoveStudent = new JFXButton();

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

        //Prepare the home pane (announcements, weather, etc)
        try {
            prepareHomePane();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    /*

    ------------------------------------

    TABBEDPANE BUTTON ACTION LISTENERS

    ------------------------------------

     */


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

    //Student Pane 2nd tab method
    @FXML
    private void addStudentActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        Stage window = new Stage();
        AnchorPane viewFullInfoPane = FXMLLoader.load(getClass().getResource("/StudentFullInfo.fxml"));
        window.setScene(new Scene(viewFullInfoPane));
        window.setResizable(false);
        window.showAndWait();

    }

    //Student Pane 2nd tab method
    @FXML
    private void removeStudentActionPerformed(ActionEvent event){
        event.consume();
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

        //This request is to let the API know it can add the access token to the blacklist
        CloseableHttpResponse result = performHttpPost("https://artemisystem.xyz/api/auth/logout");
        displayAlert("Logged out successfully!", Alert.AlertType.INFORMATION);

        //Return to login screen
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



    /*

    ------------------------------------

    PREPARATION OF DIFFERENT TABBED PANES

    ------------------------------------

     */


    private void prepareHomePane() throws IOException {

        /*
        IN THIS METHOD...

         1) ANNOUNCEMENTS ARE FETCHED AND ADDED TO THE ANNOUNCEMENTS LIST

         2) WEATHER INFO IS FETCHED AND ADDED TO THE WEATHER "WIDGET"

         */

        //First fetching the announcements

        CloseableHttpResponse announcementsResponse = performHttpGet(App.BASEURL + ANNOUCEMENTS_PATH);
        String announcementsResponseString = EntityUtils.toString(announcementsResponse.getEntity());

        //Deserialize all the announcements
        if(announcementsResponse.getStatusLine().getStatusCode() == 200){
            Gson gson = new Gson();
            Announcement[] announcements = gson.fromJson(announcementsResponseString, Announcement[].class);

            //Add each announcement to the list
            for(int i = 0; i < announcements.length; i++){
                announcementList.getItems().add(announcements[i].getSubject());
            }
        }
        else{
            displayAlert("Error fetching latest announcements. HTTP Status code: " + String.valueOf(announcementsResponse.getStatusLine().getStatusCode()), Alert.AlertType.ERROR);
        }

        //Now fetching the weather data
        CloseableHttpResponse weatherResponse = performHttpGet(App.BASEURL + WEATHER_PATH);
        String weatherResponseString = EntityUtils.toString(weatherResponse.getEntity());

        //Deserialize the weather data

        if(weatherResponse.getStatusLine().getStatusCode() == 200){
            Gson gson = new Gson();
            ForecastWeather weatherData = gson.fromJson(weatherResponseString, ForecastWeather.class);

            //Set the weather icons according to the weather data
            setWeatherImages(weatherData.getDaily());
        }
    }

    private void prepareStudentsPane() throws IOException {
        CloseableHttpResponse response = performHttpGet(App.BASEURL + STUDENT_LIST_PATH);

        if(response.getStatusLine().getStatusCode() == 200){
            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMMM yyyy HH:mm:ss zzz").create();

            studentsList = observableArrayList();

            StudentJSON[] studentJson = gson.fromJson(EntityUtils.toString(response.getEntity()), StudentJSON[].class);

            for(int i = 0; i < studentJson.length; i++){

                HashMap userDetails = studentJson[i].getUser_details();

                int id = (Integer) userDetails.get("id");
                String username = (String) userDetails.get("username");
                String email = (String) userDetails.get("email");
                Date dob = (Date) userDetails.get("dob");
                String house = (String) userDetails.get("house");
                String firstName = (String) userDetails.get("first_name");
                String lastName = (String) userDetails.get("last_name");
                String comments = (String) userDetails.get("comments");

                studentsList.add(new Student(id, username, email, dob, firstName, lastName, comments))

                //studentsList.add(new Student(studentJson))
            }

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





    @FXML
    private void viewFullInfoActionPerformed(ActionEvent event) throws IOException {

        selectedStudent = (Student) studentsTable.getSelectionModel().getSelectedItem();

        if (!(selectedStudent == null)){

            StudentFullInfo.setCurrentStudent(selectedStudent);
            AnchorPane fullInfoPane = FXMLLoader.load(getClass().getResource("/StudentFullInfo.fxml"));
            Stage viewFullInfoStage = new Stage();
            //Passing the selected students data to the view full info window
            viewFullInfoStage.setUserData(selectedStudent);
            Scene viewFullInfoScene = new Scene(fullInfoPane);
            viewFullInfoStage.setScene(viewFullInfoScene);
            viewFullInfoStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            viewFullInfoStage.initModality(Modality.APPLICATION_MODAL); //makes the window a "child" window of the admin dashboard
            viewFullInfoStage.setTitle("Full Information");
            viewFullInfoStage.showAndWait();
            prepareStudentsPane(); //refresh the table after changes are made


        }
        else{
            displayAlert("Select a student first", Alert.AlertType.ERROR);
        }

    }

    /*
      This method determines which weather icon should be used based on the day's weather. For more information
      on these weather codes, see https://openweathermap.org/weather-conditions
    */
    private Image determineWeatherImages(int id){

        if(id >= 200 && id <= 232){ // id  200 - 232 = Thunderstorm
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/11d@2x.png"));
        }
        else if(id >= 300 && id <= 321){ //id 300 - 321 = Drizzle
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/09d@2x.png"));
        }
        else if(id >= 500 && id <= 504){ //id 500 - 504 = Normal rain
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/10d@2x.png"));
        }
        else if(id == 511){ //id 511 = Freezing rain (Rare  "corner case")
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/13d@2x.png"));
        }
        else if(id >= 520 && id <= 531){ //id 520 - 531 = Shower rain
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/09d@2x.png"));
        }
        else if(id >= 600 && id <= 622){ //id 600 - 622 = Snow
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/13d@2x.png"));
        }
        else if(id >= 701 && id <= 781){ //id 701 - 781 = Atmosphere (like mist, or ash)
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/50d@2x.png"));
        }
        else if(id == 800){ //id 800 = Clear sky
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/01d@2x.png"));
        }
        else if(id == 801){ //id 801 = Few clouds
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/02d@2x.png"));
        }
        else if(id == 802){ //id 802 = Scattered clouds
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/03d@2x.png"));
        }
        else if(id == 803 || id == 804){ //id 803 - 804 = Broken and Overcast clouds
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/04d@2x.png"));
        }
        return null;
    }

    //This method takes in each day's weather and sets its icon in the weather "widget" accordingly
    private void setWeatherImages(Daily[] dailyWeather){

        Weather[] weather1 = dailyWeather[0].getWeather();
        todayImage.setImage(determineWeatherImages(weather1[0].getId()));

        Weather[] weather2 = dailyWeather[1].getWeather();
        secondDayImage.setImage(determineWeatherImages(weather2[0].getId()));

        Weather[] weather3 = dailyWeather[2].getWeather();
        thirdDayImage.setImage(determineWeatherImages(weather3[0].getId()));

        Weather[] weather4 = dailyWeather[3].getWeather();
        fourthDayImage.setImage(determineWeatherImages(weather4[0].getId()));

        Weather[] weather5 = dailyWeather[4].getWeather();
        fifthDayImage.setImage(determineWeatherImages(weather5[0].getId()));

        Weather[] weather6 = dailyWeather[5].getWeather();
        sixthDayImage.setImage(determineWeatherImages(weather6[0].getId()));

        Weather[] weather7 = dailyWeather[6].getWeather();
        seventhDayImage.setImage(determineWeatherImages(weather7[0].getId()));
    }


    private void displayAlert(String content, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        alert.showAndWait();
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

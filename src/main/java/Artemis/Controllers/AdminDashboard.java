package Artemis.Controllers;


import Artemis.App;
import Artemis.Models.Announcement;
import Artemis.Models.JSON.Serializers.StudentJSON;
import Artemis.Models.JSON.Serializers.TeacherJSON;
import Artemis.Models.Student;
import Artemis.Models.Teacher;
import Artemis.Models.Weather.Daily;
import Artemis.Models.Weather.ForecastWeather;
import Artemis.Models.Weather.Weather;
import com.calendarfx.view.YearMonthView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import io.github.palexdev.materialfx.*;

import static javafx.collections.FXCollections.observableArrayList;


public class AdminDashboard extends Application implements Initializable {


    private static String accessToken;
    private static int userId;
    private boolean studentsPanePrepared = false;
    private boolean teachersPanePrepared = false;

    private final String ANNOUCEMENTS_PATH = "api/announcements/";
    private final String WEATHER_PATH = "api/weather";
    private final String TEACHERS_PATH = "api/teachers";
    private ObservableList<Teacher> teachersList;
    private ObservableList<Student> studentsList;

    CloseableHttpClient client = HttpClients.createDefault();

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
    private MFXButton btnAddStudent = new MFXButton();
    @FXML
    private MFXButton btnRemoveStudent = new MFXButton();


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
    Pane adminPane = new Pane();
    @FXML
    Button signOut = new Button();

    //Students Pane Componenets

    @FXML
    MFXTableView<Student> studentsTable = new MFXTableView<>();
    @FXML
    MFXTableView<Teacher> teachersTable = new MFXTableView<>();


    @FXML
    MFXButton btnViewFullInfo = new MFXButton();
    @FXML
    MFXButton btnRefreshStudents = new MFXButton();





    //2nd Tab of Students Pane components



    final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");





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

    /**
     * Executes an HTTP POST request to the supplied URL, and returns the response
     * @param url
     * @return CloseableHttpResponse
     * @throws IOException
     */

    private CloseableHttpResponse performHttpPost(String url) throws  IOException{

        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        try{
            CloseableHttpResponse response = client.execute(request);
            return response;
        }
        catch (ConnectException e){
            displayAlert("Error connecting to server", Alert.AlertType.ERROR);
        }
        finally {
            System.out.println();
        }
        return null;
    }

    /**
     * Executes a HTTP GET request to the supplied URL, and returns the response
     * @param url
     * @return CloseableHttpResponse
     * @throws IOException
     */

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

    private CloseableHttpResponse performHttpDelete(String url) throws IOException{
        HttpDelete request = new HttpDelete(url);
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


    private int fetchDayOfWeek(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    /**
     * Returns the current day of the week as a string, such as "Saturday"
     * @param date
     * @return dayString
     */

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
    private void studentsActionPerformed(ActionEvent event) throws IOException, ParseException {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(studentsPane);

        if (!studentsPanePrepared) {

            prepareStudentsPane();
            studentsPanePrepared = true;
        }
    }

    @FXML
    private void adminPanelActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(adminPane);
    }



    //Student Pane 2nd tab method
    @FXML
    private void addStudentActionPerformed(ActionEvent event) throws IOException {
        event.consume();

        StudentFullInfo.postRequest = true;

        Stage window = new Stage();
        AnchorPane viewFullInfoPane = FXMLLoader.load(getClass().getResource("/StudentFullInfo.fxml"));
        window.setScene(new Scene(viewFullInfoPane));
        window.setResizable(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
        StudentFullInfo.postRequest = false;

    }

    /**
     * Removes a selected student from the database through an HTTP DELETE request
     * @param event
     * @throws IOException
     */

    @FXML
    private void removeStudentActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();

        if(selectedStudent == null){
            displayAlert("Select a student to be removed from the table first", Alert.AlertType.ERROR);
        }
        else{
            Optional<ButtonType> result = displayAlert("Are you sure you would like to permanently delete a student with the ID: \"" +
                    selectedStudent.getId() + "\" and the last name \"" + selectedStudent.getLastName() + "\""
                    + " from the system?", Alert.AlertType.CONFIRMATION);

            if (result.get() == ButtonType.OK){
                performHttpDelete(App.BASEURL + App.STUDENT_LIST_PATH + selectedStudent.getId());
            }
        }

    }

    @FXML
    private void teachersActionPerformed(ActionEvent event) throws IOException, ParseException {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(teachersPane);

        if(!teachersPanePrepared) {
            prepareTeachersPane();
        }
    }

    @FXML
    private void timetablesActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(timetablesPane);
    }

    /**
     * Closes the window, notifies the API it can invalidate the access token,
     * and returns to the login screen
     * @param event
     * @throws IOException
     */
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
        window.getIcons().add(new Image(App.class.getResourceAsStream("/Images/ArtemisAlpha.png")));
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

    /**
     * Prepares the home pane for use by fetching announcements and weather data from the API
     * @throws IOException
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

        EntityUtils.consume(announcementsResponse.getEntity());
        announcementsResponse.close();

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

        EntityUtils.consume(weatherResponse.getEntity());
        weatherResponse.close();

        //Deserialize the weather data

        if(weatherResponse.getStatusLine().getStatusCode() == 200){
            Gson gson = new Gson();
            ForecastWeather weatherData = gson.fromJson(weatherResponseString, ForecastWeather.class);

            //Set the weather icons according to the weather data
            setWeatherImages(weatherData.getDaily());
        }
    }

    /**
     * Prepares the Students Pane for use by fetching all necessary data from the
     * API, to populate the students table
     * @throws IOException
     * @throws ParseException
     */

    private void prepareStudentsPane() throws IOException, ParseException {
        CloseableHttpResponse response = performHttpGet(App.BASEURL + App.STUDENT_LIST_PATH);

        if(response.getStatusLine().getStatusCode() == 200){
            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMMM yyyy HH:mm:ss zzz").create();

            studentsList = FXCollections.observableArrayList();
            StudentJSON[] studentJson = gson.fromJson(EntityUtils.toString(response.getEntity()), StudentJSON[].class);

            EntityUtils.consume(response.getEntity());
            response.close();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for(int i = 0; i < studentJson.length; i++){

                HashMap userDetails = studentJson[i].getUser_details();

                int id = Integer.parseInt((String) userDetails.get("id")) ;
                String username = (String) userDetails.get("username");
                String email = (String) userDetails.get("email");
                String dob = (String) userDetails.get("dob");
                String house = (String) userDetails.get("house");
                String firstName = (String) userDetails.get("first_name");
                String lastName = (String) userDetails.get("last_name");
                String comments = (String) userDetails.get("comments");

                int form = studentJson[i].getForm();
                String primaryContactName = studentJson[i].getPrimary_contact_name();
                String primaryContactEmail = studentJson[i].getPrimary_contact_email();
                String secondaryContactName = studentJson[i].getSecondary_contact_name();
                String secondaryContactEmail = studentJson[i].getSecondary_contact_email();

                studentsList.add(new Student(id, firstName, lastName, username, dateFormat.parse(dob), house, form, email, comments, primaryContactName, primaryContactEmail, secondaryContactName, secondaryContactEmail));

            }

            MFXTableColumn<Student> colID = new MFXTableColumn<>("ID", Comparator.comparing(Student::getId));
            MFXTableColumn<Student> colFirstName = new MFXTableColumn<>("First Name", Comparator.comparing(Student::getFirstName));
            MFXTableColumn<Student> colLastName = new MFXTableColumn<>("Last Name", Comparator.comparing(Student::getLastName));
            MFXTableColumn<Student> colForm = new MFXTableColumn<>("Form", Comparator.comparing(Student::getForm));
            MFXTableColumn<Student> colHouse = new MFXTableColumn<>("House", Comparator.comparing(Student::getHouse));
            MFXTableColumn<Student> colEmail = new MFXTableColumn<>("Email", Comparator.comparing(Student::getEmail));

            colID.setRowCellFunction(student ->  new MFXTableRowCell(String.valueOf(student.getId())));
            colFirstName.setRowCellFunction(student -> new MFXTableRowCell(student.getFirstName()));
            colLastName.setRowCellFunction(student -> new MFXTableRowCell(student.getLastName()));
            colForm.setRowCellFunction(student -> new MFXTableRowCell(String.valueOf(student.getForm())));
            colHouse.setRowCellFunction(student -> new MFXTableRowCell(student.getHouse()));
            colEmail.setRowCellFunction(student -> new MFXTableRowCell(student.getEmail()));

            studentsTable.setItems(studentsList);
            colID.setMaxWidth(10);
            studentsTable.getTableColumns().addAll(colID, colFirstName, colLastName, colForm, colHouse, colEmail);

        }
        else if(response.getStatusLine().getStatusCode() == 401){
            displayAlert("Unauthorized, please re-authenticate", Alert.AlertType.ERROR);
        }
        else if(response.getStatusLine().getStatusCode() == 403){
            displayAlert("Forbidden, please re-authenticate", Alert.AlertType.ERROR);
        }
    }

    private void prepareTeachersPane() throws IOException, ParseException {

        CloseableHttpResponse response = performHttpGet(App.BASEURL + TEACHERS_PATH);

        if(response.getStatusLine().getStatusCode() == 200) {

            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMMM yyyy HH:mm:ss zzz").create();
            teachersList = FXCollections.observableArrayList();
            TeacherJSON[] teacherJSON = gson.fromJson(EntityUtils.toString(response.getEntity()), TeacherJSON[].class);
            EntityUtils.consume(response.getEntity());
            response.close();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for(int i = 0; i < teacherJSON.length; i++) {

                HashMap userDetails = teacherJSON[i].getUser_details();

                int id = Integer.parseInt((String) userDetails.get("id"));
                String username = (String) userDetails.get("username");
                String email = (String) userDetails.get("email");
                String dob = (String) userDetails.get("dob");
                String house = (String) userDetails.get("house");
                String firstName = (String) userDetails.get("first_name");
                String lastName = (String) userDetails.get("last_name");
                String comments = (String) userDetails.get("comments");
                String subject = teacherJSON[i].getSubject();

                teachersList.add(new Teacher(id, firstName, lastName, username, dateFormat.parse(dob), house, email, comments, subject));

            }

            MFXTableColumn<Teacher> colID = new MFXTableColumn<>("ID", Comparator.comparing(Teacher::getId));
            MFXTableColumn<Teacher> colFirstName = new MFXTableColumn<>("First Name", Comparator.comparing(Teacher::getFirstName));
            MFXTableColumn<Teacher> colLastName = new MFXTableColumn<>("Last Name", Comparator.comparing(Teacher::getLastName));
            MFXTableColumn<Teacher> colSubject = new MFXTableColumn<>("Subject", Comparator.comparing(Teacher::getSubject));
            MFXTableColumn<Teacher> colHouse = new MFXTableColumn<>("House", Comparator.comparing(Teacher::getHouse));
            MFXTableColumn<Teacher> colEmail = new MFXTableColumn<>("Email", Comparator.comparing(Teacher::getEmail));

            colID.setRowCellFunction(teacher ->  new MFXTableRowCell(String.valueOf(teacher.getId())));
            colFirstName.setRowCellFunction(teacher -> new MFXTableRowCell(teacher.getFirstName()));
            colLastName.setRowCellFunction(teacher -> new MFXTableRowCell(teacher.getLastName()));
            colSubject.setRowCellFunction(teacher -> new MFXTableRowCell(String.valueOf(teacher.getSubject())));
            colHouse.setRowCellFunction(teacher -> new MFXTableRowCell(teacher.getHouse()));
            colEmail.setRowCellFunction(teacher -> new MFXTableRowCell(teacher.getEmail()));

            teachersTable.setItems(teachersList);
            teachersTable.getTableColumns().addAll(colID, colFirstName, colLastName, colSubject, colHouse, colEmail);

        }
        else if(response.getStatusLine().getStatusCode() == 401){
            displayAlert("Unauthorized, please re-authenticate", Alert.AlertType.ERROR);
        }
        else if(response.getStatusLine().getStatusCode() == 403){
            displayAlert("Forbidden, please re-authenticate", Alert.AlertType.ERROR);
        }
    }


    /**
     * Fetches updated data for the students table from the API
     * @param event
     */

    @FXML
    private void refreshStudentsActionPerformed(ActionEvent event) throws IOException, ParseException {
        prepareStudentsPane();
    }

    /**
     * Opens a panel where the full information about a student can be viewed or edited.
     * This panel is also used to enter details when adding a new student
     * @param event
     * @throws IOException
     * @throws ParseException
     */

    @FXML
    private void viewStudentFullInfoActionPerformed(ActionEvent event) throws IOException, ParseException {

        event.consume();
        StudentFullInfo.postRequest = false;
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();

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
            StudentFullInfo.setCurrentStudent(null); //resetting the current student to null so all fields are cleared
            prepareStudentsPane(); //refresh the table after changes are made


        }
        else{
            displayAlert("Select a student first", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void viewTeacherFullInfoActionPerformed(ActionEvent event) throws IOException {

        event.consume();
        TeacherFullInfo.postRequest = false;
        Teacher selectedTeacher = teachersTable.getSelectionModel().getSelectedItem();

        if(!(selectedTeacher == null)) {

            TeacherFullInfo.setCurrentTeacher(selectedTeacher);
            AnchorPane fullInfoPane = FXMLLoader.load(getClass().getResource("/TeacherFullInfo.fxml"));
            Stage viewFullInfoStage = new Stage();
            viewFullInfoStage.setUserData(selectedTeacher);
            Scene viewFullInfoScene = new Scene(fullInfoPane);
            viewFullInfoStage.setScene(viewFullInfoScene);
            viewFullInfoStage.initModality(Modality.APPLICATION_MODAL);
            viewFullInfoStage.setTitle("Full Information");
            viewFullInfoStage.showAndWait();
            TeacherFullInfo.setCurrentTeacher(null); //resetting the current student to null so all fields are cleared

        }

    }

    @FXML
    private void publishNewAnnouncementActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        PublishNewAnnouncement.setAccessToken(accessToken);
        AnchorPane newAnnouncementPane = FXMLLoader.load(getClass().getResource("/PublishAnnouncement.fxml"));
        Stage newAnnouncementStage = new Stage();
        Scene newAnnouncementScene = new Scene(newAnnouncementPane);
        newAnnouncementStage.setScene(newAnnouncementScene);
        newAnnouncementStage.initModality(Modality.APPLICATION_MODAL);
        newAnnouncementStage.setTitle("Publish a new announcement");
        newAnnouncementStage.showAndWait();
    }

    @FXML
    private void addTeacherActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        TeacherFullInfo.postRequest = true;
        AnchorPane teacherFullInfoPane = FXMLLoader.load(getClass().getResource("/TeacherFullInfo.fxml"));
        Stage teacherFullInfoStage = new Stage();
        Scene teacherFullInfoScene = new Scene(teacherFullInfoPane);
        teacherFullInfoStage.setScene(teacherFullInfoScene);
        teacherFullInfoStage.initModality(Modality.APPLICATION_MODAL);
        teacherFullInfoStage.showAndWait();
        TeacherFullInfo.postRequest = false;
    }

    @FXML
    private void removeTeacherActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        Teacher selectedTeacher = teachersTable.getSelectionModel().getSelectedItem();

        if(selectedTeacher == null){
            displayAlert("Select a teacher to be removed from the table first", Alert.AlertType.ERROR);
        }
        else{
            Optional<ButtonType> result = displayAlert("Are you sure you would like to permanently delete a teacher with the ID: \"" +
                    selectedTeacher.getId() + "\" and the last name \"" + selectedTeacher.getLastName() + "\""
                    + " from the system?", Alert.AlertType.CONFIRMATION);

            if (result.get() == ButtonType.OK){
                performHttpDelete(App.BASEURL + App.STUDENT_LIST_PATH + selectedTeacher.getId());
            }
        }

    }




    /**
     * Determines which weather icon should be used based on the day's weather. For more information
     * on these weather codes, see https://openweathermap.org/weather-conditions
     * @param weatherID
     * @return weatherIcon
     */

    private Image determineWeatherImages(int weatherID){

        if(weatherID >= 200 && weatherID <= 232){ // id  200 - 232 = Thunderstorm
            return new Image(App.class.getResourceAsStream("/Images/Weather/11d@2x.png"));
        }
        else if(weatherID >= 300 && weatherID <= 321){ //id 300 - 321 = Drizzle
            return new Image(App.class.getResourceAsStream("/Images/Weather/09d@2x.png"));
        }
        else if(weatherID >= 500 && weatherID <= 504){ //id 500 - 504 = Normal rain
            return new Image(App.class.getResourceAsStream("/Images/Weather/10d@2x.png"));
        }
        else if(weatherID == 511){ //id 511 = Freezing rain (Rare  "corner case")
            return new Image(App.class.getResourceAsStream("/Images/Weather/13d@2x.png"));
        }
        else if(weatherID >= 520 && weatherID <= 531){ //id 520 - 531 = Shower rain
            return new Image(App.class.getResourceAsStream("/Images/Weather/09d@2x.png"));
        }
        else if(weatherID >= 600 && weatherID <= 622){ //id 600 - 622 = Snow
            return new Image(App.class.getResourceAsStream("/Images/Weather/13d@2x.png"));
        }
        else if(weatherID >= 701 && weatherID <= 781){ //id 701 - 781 = Atmosphere (like mist, or ash)
            return new Image(App.class.getResourceAsStream("/Images/Weather/50d@2x.png"));
        }
        else if(weatherID == 800){ //id 800 = Clear sky
            return new Image(App.class.getResourceAsStream("/Images/Weather/01d@2x.png"));
        }
        else if(weatherID == 801){ //id 801 = Few clouds
            return new Image(App.class.getResourceAsStream("/Images/Weather/02d@2x.png"));
        }
        else if(weatherID == 802){ //id 802 = Scattered clouds
            return new Image(App.class.getResourceAsStream("/Images/Weather/03d@2x.png"));
        }
        else if(weatherID == 803 || weatherID == 804){ //id 803 - 804 = Broken and Overcast clouds
            return new Image(App.class.getResourceAsStream("/Images/Weather/04d@2x.png"));
        }
        return null;
    }


    /**
     * Takes in each day's weather and sets its icon in the weather "widget" accordingly
     * @param dailyWeather
     */

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

    /**
     * Displays a JAVAFX Alert (similar to a swing JOptionPane)
     * @param content
     * @param alertType
     */

    private Optional displayAlert(String content, Alert.AlertType alertType){
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

    //GETTERS AND SETTERS

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

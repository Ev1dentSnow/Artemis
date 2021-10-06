package Artemis.Controllers;


import Artemis.App;
import Artemis.Models.*;
import Artemis.Models.JSON.Serializers.AssignMarkJSON;
import Artemis.Models.JSON.Serializers.StudentClassJSON;
import Artemis.Models.JSON.Serializers.StudentJSON;
import Artemis.Models.JSON.Serializers.TeacherJSON;
import Artemis.Models.Weather.Daily;
import Artemis.Models.Weather.ForecastWeather;
import Artemis.Models.Weather.Weather;
import com.calendarfx.view.YearMonthView;
import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;


public class AdminDashboard extends Application implements Initializable {

    //permission level 1 -> teachers ; permission level 2 -> admins
    private static int permissionLevel;
    private static String accessToken;
    private static int userId;
    private boolean studentsPanePrepared = false;
    private boolean teachersPanePrepared = false;
    private boolean editMarksPanePrepared = false;
    private boolean enterMarksPanePrepared = false;
    private final String ANNOUCEMENTS_PATH = "api/announcements/";
    private final String WEATHER_PATH = "api/weather";
    private final String TEACHERS_PATH = "api/teachers/";
    private ObservableList<Teacher> teachersList;
    private ObservableList<Student> studentsList;

    @FXML
    private ListView<Announcement> announcementList;
    private Announcement selectedAnnouncement;
    private StudentClassJSON selectedClass;

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
    Pane marksHomePane = new Pane();
    @FXML
    Pane editMarksPane = new Pane();
    @FXML
    Pane enterMarksPane = new Pane();
    @FXML
    Pane adminPane = new Pane();
    @FXML
    Button signOut = new Button();
    @FXML
    Label lblUserRole = new Label();
    @FXML
    MFXTableView<Student> studentsTable = new MFXTableView<>();
    @FXML
    MFXTableView<Teacher> teachersTable = new MFXTableView<>();
    @FXML
    MFXButton btnViewFullInfo = new MFXButton();
    @FXML
    MFXButton btnRefreshStudents = new MFXButton();
    @FXML
    private ListView lstClassList = new ListView();
    @FXML
    private ListView lstStudentsList = new ListView();
    @FXML
    private ListView lstAssignmentsList = new ListView();
    @FXML
    private ListView lstClassList1 = new ListView();
    @FXML
    private ListView lstStudentsList1 = new ListView();
    @FXML
    private ListView lstAssignmentsList1 = new ListView();
    @FXML
    private MFXButton btnEditExistingMarks = new MFXButton();
    @FXML
    private MFXButton btnEnterNewMarks = new MFXButton();

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
        } catch (IOException | UnirestException e) {
            e.printStackTrace();
        }

    }

    /**
     * Executes a HTTP GET request to the supplied URL, and returns the response
     * @param url
     * @return CloseableHttpResponse
     * @throws IOException
     */

    private HttpResponse performHttpGet(String url) throws UnirestException {

        return Unirest.get(url)
                .header("Authorization", "Bearer " + accessToken)
                .asJson();
    }

    private HttpResponse performHttpPatch(String url, String jsonBody) throws UnirestException {

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Content-Type", "application/json");
        requestHeaders.put("Authorization", "Bearer " + accessToken);

        return Unirest.patch(url)
                .headers(requestHeaders)
                .body(jsonBody)
                .asJson();
    }

    private HttpResponse performHttpDelete(String url) throws UnirestException {

        return Unirest.delete(url)
               .header("Authorization", "Bearer " + accessToken)
               .asString();

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
    private void studentsActionPerformed(ActionEvent event) throws IOException, ParseException, UnirestException {

        if (permissionLevel == 2) {

            event.consume();
            stackPane.getChildren().clear();
            stackPane.getChildren().add(studentsPane);

            if (!studentsPanePrepared) {

                prepareStudentsPane();
                studentsPanePrepared = true;
            }
        }
        else {
            displayAlert("Only Admins may access this page", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void marksActionPerformed(ActionEvent event) {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(marksHomePane);
    }

    @FXML
    private void enterNewMarksActionPerformed(ActionEvent event) {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(enterMarksPane);
        if (!enterMarksPanePrepared) {
            prepareEnterMarksPane();
            enterMarksPanePrepared = true;
        }
    }

    @FXML
    private void editExistingMarksActionPerformed(ActionEvent event) {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(editMarksPane);
        if (!editMarksPanePrepared) {
            prepareEditMarksPane();
            editMarksPanePrepared = true;
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
    private void removeStudentActionPerformed(ActionEvent event) throws UnirestException {
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
                HttpResponse response = performHttpDelete(App.BASEURL + App.STUDENT_LIST_PATH + selectedStudent.getId());

                if (response.getStatus() == 204) {
                    displayAlert("Student deleted!", Alert.AlertType.INFORMATION);
                }
                else {
                    displayAlert("Error deleting student", Alert.AlertType.ERROR);
                }
            }
        }

    }

    @FXML
    private void teachersActionPerformed(ActionEvent event) throws IOException, ParseException, UnirestException {

        if (permissionLevel == 2) {
            event.consume();
            stackPane.getChildren().clear();
            stackPane.getChildren().add(teachersPane);

            if (!teachersPanePrepared) {
                prepareTeachersPane();
            }

        }
        else {
            displayAlert("Insufficient permissions, only admins may access this", Alert.AlertType.ERROR);
        }
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
        //CloseableHttpResponse result = performHttpPost("https://artemisystem.xyz/api/auth/logout");
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

    private void prepareHomePane() throws IOException, UnirestException {

        if (permissionLevel == 1) {
            lblUserRole.setText("Teacher");
        }
        else {
            lblUserRole.setText("Administrator");
        }

        /*
        IN THIS METHOD...

         1) ANNOUNCEMENTS ARE FETCHED AND ADDED TO THE ANNOUNCEMENTS LIST

         2) WEATHER INFO IS FETCHED AND ADDED TO THE WEATHER "WIDGET"

         */

        //First fetching the announcements
        announcementList.getItems().clear();
        HttpResponse announcementsResponse = performHttpGet(App.BASEURL + ANNOUCEMENTS_PATH);
        String announcementsResponseString = announcementsResponse.getBody().toString();

        if(announcementsResponse.getStatus() == 200){
            Gson gson = new Gson();
            Announcement[] announcements = gson.fromJson(announcementsResponseString, Announcement[].class);

            //Add each announcement to the list
            for(int i = 0; i < announcements.length; i++){
                announcementList.getItems().add(announcements[i]);
            }
        }
        else{
            displayAlert("Error fetching latest announcements. HTTP Status code: " + announcementsResponse.getStatus(), Alert.AlertType.ERROR);
        }

        //Now fetching the weather data
        HttpResponse weatherResponse = performHttpGet(App.BASEURL + WEATHER_PATH);
        String weatherResponseString = weatherResponse.getBody().toString();

        //Deserialize the weather data

        if(weatherResponse.getStatus() == 200){
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

    private void prepareStudentsPane() throws IOException, ParseException, UnirestException {
        HttpResponse response = performHttpGet(App.BASEURL + App.STUDENT_LIST_PATH);

        if(response.getStatus() == 200){
            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMMM yyyy HH:mm:ss zzz").create();

            studentsList = FXCollections.observableArrayList();
            StudentJSON[] studentJson = gson.fromJson(response.getBody().toString(), StudentJSON[].class);

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
        else if(response.getStatus() == 401){
            displayAlert("Unauthorized, please re-authenticate", Alert.AlertType.ERROR);
        }
        else if(response.getStatus() == 403){
            displayAlert("Forbidden, please re-authenticate", Alert.AlertType.ERROR);
        }
    }

    private void prepareTeachersPane() throws IOException, ParseException, UnirestException {

        HttpResponse response = performHttpGet(App.BASEURL + TEACHERS_PATH);

        if(response.getStatus() == 200) {

            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMMM yyyy HH:mm:ss zzz").create();
            teachersList = FXCollections.observableArrayList();
            TeacherJSON[] teacherJSON = gson.fromJson(response.getBody().toString(), TeacherJSON[].class);

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
        else if(response.getStatus() == 401){
            displayAlert("Unauthorized, please re-authenticate", Alert.AlertType.ERROR);
        }
        else if(response.getStatus() == 403){
            displayAlert("Forbidden, please re-authenticate", Alert.AlertType.ERROR);
        }
    }

    private void prepareEditMarksPane() {

        HttpResponse response = null;
        try {
            response = performHttpGet(App.BASEURL + TEACHERS_PATH + userId + "/classes");
        } catch (UnirestException e) {
            displayAlert("There was an error fetching marks from the server", Alert.AlertType.ERROR);
        }

        if (response.getStatus() == 200) {
            StudentClassJSON.setToStringNumber(0);
            Gson gson = new Gson();
            StudentClassJSON[] studentClassJSONs = gson.fromJson(response.getBody().toString(), StudentClassJSON[].class);

            for (StudentClassJSON currentClass : studentClassJSONs) {
                lstClassList.getItems().add(currentClass);
            }
        }
    }

    private void prepareEnterMarksPane() {

        HttpResponse response = null;
        try {
            response = performHttpGet(App.BASEURL + TEACHERS_PATH + userId + "/classes");
        } catch (UnirestException e) {
            displayAlert("There was an error fetching marks from the server", Alert.AlertType.ERROR);
        }

        if (response.getStatus() == 200) {
            StudentClassJSON.setToStringNumber(0);
            Gson gson = new Gson();
            StudentClassJSON[] studentClassJSONs = gson.fromJson(response.getBody().toString(), StudentClassJSON[].class);

            for (StudentClassJSON currentClass : studentClassJSONs) {
                lstClassList1.getItems().add(currentClass);
            }
        }

    }


    @FXML
    private void classListClicked(MouseEvent event) throws UnirestException {

        event.consume();
        lstStudentsList.getItems().clear();



        //Checks whether the user has selected a different class from the class list or not
        if (selectedClass != lstClassList.getSelectionModel().getSelectedItem()) {
            StudentClassJSON.setToStringNumber(0);
        }

        //Add only students that are part of the selected class to the students list

        StudentClassJSON.setToStringNumber(1);
        for (int i = 0; i < lstClassList.getItems().size(); i++) {
            StudentClassJSON currentStudent = (StudentClassJSON) lstClassList.getItems().get(i);

            StudentClassJSON currentClass = (StudentClassJSON) lstClassList.getSelectionModel().getSelectedItem();
            if (currentStudent.getClasss().getId() == currentClass.getClasss().getId()) {
                lstStudentsList.getItems().add(currentStudent);
            }

        }
        selectedClass = (StudentClassJSON) lstClassList.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void studentsListClicked(MouseEvent event) {

        event.consume();
        lstAssignmentsList.getItems().clear();
        StudentClassJSON current = (StudentClassJSON) lstStudentsList.getSelectionModel().getSelectedItem();
        String studentID = current.getStudent().getUser_details().get("id");

        HttpResponse response = null;
        try {
            response = performHttpGet(App.BASEURL + App.STUDENT_LIST_PATH + studentID + "/marks");
        } catch (UnirestException e) {
            displayAlert("Error fetching data from server", Alert.AlertType.ERROR);
        }

        if (response.getStatus() == 200) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    //registering this type adapter allows the date to be deserialized into a LocalDate instead of a Date, preventing a runtime exception
                    .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>(){
                        @Override
                        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                        }
                    }).create();

            Marks[] marks = gson.fromJson(response.getBody().toString(), Marks[].class);

            for (Marks mark : marks) {
                lstAssignmentsList.getItems().add(mark);
            }
        }
        else {
            displayAlert("Error fetching data from server", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void assignmentsListClicked(MouseEvent event) {
        event.consume();
        Marks selectedMark = (Marks) lstAssignmentsList.getSelectionModel().getSelectedItem();
        if (selectedMark != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Edit mark");
            dialog.setHeaderText("Current Mark: " + selectedMark.getMarkAwarded() + "/" + selectedMark.getAssignment().getMaxMarks());
            dialog.setContentText("New mark (only quantity of marks to be awarded without a slash): ");


            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                selectedMark.setMarkAwarded(Double.parseDouble(result.get()));
                StudentClassJSON currentStudent = (StudentClassJSON) lstStudentsList.getSelectionModel().getSelectedItem();

                HttpResponse response = null;
                AssignMarkJSON assignMarkJSON = new AssignMarkJSON();
                assignMarkJSON.setAssignment_id(selectedMark.getAssignment().getId());
                assignMarkJSON.setStudent_id(Integer.parseInt(currentStudent.getStudent().getUser_details().get("id")));
                assignMarkJSON.setClass_id(selectedMark.getClassId());
                assignMarkJSON.setMark_awarded(selectedMark.getMarkAwarded());

                Gson gson = new Gson();
                String jsonBody = gson.toJson(assignMarkJSON);

                try {
                    response = performHttpPatch(App.BASEURL + App.STUDENT_LIST_PATH + currentStudent.getStudent().getUser_details().get("id") + "/marks/" + selectedMark.getId(), jsonBody);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                if (response.getStatus() == 200) {
                    displayAlert("Mark updated successfully", Alert.AlertType.INFORMATION);
                }
                else {
                    displayAlert("Error sending data to the server", Alert.AlertType.ERROR);
                }
            }
        }
    }


    /**
     * Fetches updated data for the students table from the API
     * @param event
     */

    @FXML
    private void refreshStudentsActionPerformed(ActionEvent event) throws IOException, ParseException, UnirestException {
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
    private void viewStudentFullInfoActionPerformed(ActionEvent event) throws IOException, ParseException, UnirestException {

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
    private void publishNewAnnouncementActionPerformed(ActionEvent event) throws IOException, UnirestException {
        event.consume();
        PublishNewAnnouncement.setAccessToken(accessToken);
        AnchorPane newAnnouncementPane = FXMLLoader.load(getClass().getResource("/PublishAnnouncement.fxml"));
        Stage newAnnouncementStage = new Stage();
        Scene newAnnouncementScene = new Scene(newAnnouncementPane);
        newAnnouncementStage.setScene(newAnnouncementScene);
        newAnnouncementStage.initModality(Modality.APPLICATION_MODAL);
        newAnnouncementStage.setTitle("Publish a new announcement");
        newAnnouncementStage.showAndWait();
        prepareHomePane();
    }

    @FXML
    private void deleteSelectedAnnouncementActionPerformed(ActionEvent event) throws UnirestException, IOException {
        event.consume();
        selectedAnnouncement = announcementList.getSelectionModel().getSelectedItem();

        if (selectedAnnouncement != null) {
            HttpResponse response = performHttpDelete(App.BASEURL + ANNOUCEMENTS_PATH + selectedAnnouncement.getId());

            if (response.getStatus() == 204) {
                displayAlert("Announcement deleted!", Alert.AlertType.INFORMATION);
                prepareHomePane();
            }
            else {
                displayAlert("Error deleting announcement", Alert.AlertType.ERROR);
            }
            displayAlert("Announcement deleted!", Alert.AlertType.INFORMATION);
        }

        else {
            displayAlert("Select an announcement from the Home Panel first", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void deleteAllAnnouncementsActionPerformed(ActionEvent event) throws UnirestException, IOException {
        event.consume();
        HttpResponse response = performHttpDelete(App.BASEURL + ANNOUCEMENTS_PATH);

        if (response.getStatus() == 204) {
            displayAlert("All announcements purged!", Alert.AlertType.INFORMATION);
            prepareHomePane();
        }
        else {
            displayAlert("Error purging all announcements", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void deleteDotActionPerformed(ActionEvent event) {
        event.consume();
        HttpResponse response = null;
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        int studentID = 0;

        if (selectedStudent != null) {
            studentID = selectedStudent.getId();
            try {
                response = performHttpDelete(App.BASEURL + App.STUDENT_LIST_PATH + studentID + "/dots");
                displayAlert("Dot removed successfully", Alert.AlertType.INFORMATION);

            } catch (UnirestException e) {
                displayAlert("Error fetching data from server", Alert.AlertType.ERROR);
            }
        }
        else {
            displayAlert("Select a student from the students table first", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void createNewDotActionPerformed(ActionEvent event) {
        event.consume();
        CloseableHttpResponse response = null;
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        int studentID = 0;

        if (selectedStudent != null) {
            studentID = selectedStudent.getId();

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add new dot");
            dialog.setContentText("Enter reason: ");

            Optional<String> reason = dialog.showAndWait();

            Teacher assigningTeacher = new Teacher();
            assigningTeacher.setId(userId);
            Dots newDot = new Dots(0, reason.get(), selectedStudent.getId(), assigningTeacher);

            Gson gson = new Gson();
            String jsonBody = gson.toJson(newDot);

            try {
                response = performHttpPost(App.BASEURL + App.STUDENT_LIST_PATH + studentID + "/dots/", jsonBody);
            }
            catch (IOException e) {
                displayAlert("Error fetching data from server", Alert.AlertType.ERROR);
            }
        }
        else {
            displayAlert("Select a student from the students table first", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void createNewAssignmentActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        CreateNewAssignment.setAccessToken(accessToken);
        CreateNewAssignment.setTeacherID(userId);
        Stage stage = new Stage();
        AnchorPane ap = FXMLLoader.load(getClass().getResource("/CreateAssignment.fxml"));
        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    }

    @FXML
    private void addTeacherActionPerformed(ActionEvent event) throws IOException, UnirestException, ParseException {
        event.consume();
        TeacherFullInfo.postRequest = true;
        AnchorPane teacherFullInfoPane = FXMLLoader.load(getClass().getResource("/TeacherFullInfo.fxml"));
        Stage teacherFullInfoStage = new Stage();
        Scene teacherFullInfoScene = new Scene(teacherFullInfoPane);
        teacherFullInfoStage.setScene(teacherFullInfoScene);
        teacherFullInfoStage.initModality(Modality.APPLICATION_MODAL);
        teacherFullInfoStage.showAndWait();
        TeacherFullInfo.postRequest = false;
        prepareTeachersPane();
    }

    @FXML
    private void removeTeacherActionPerformed(ActionEvent event) throws UnirestException {
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
                HttpResponse response = performHttpDelete(App.BASEURL + App.STUDENT_LIST_PATH + selectedTeacher.getId());

                if (response.getStatus() == 204) {
                    displayAlert("Teacher deleted!", Alert.AlertType.INFORMATION);
                }
                else {
                    displayAlert("Error deleting teacher", Alert.AlertType.ERROR);
                }
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


    public static int getPermissionLevel() {
        return permissionLevel;
    }

    public static void setPermissionLevel(int permissionLevel) {
        AdminDashboard.permissionLevel = permissionLevel;
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

    private CloseableHttpResponse performHttpPost(String url, String jsonData) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        StringEntity entity = new StringEntity(jsonData);
        request.setEntity(entity);

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

}

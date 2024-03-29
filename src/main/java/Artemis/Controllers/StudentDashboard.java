package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.*;
import Artemis.Models.JSON.Serializers.StudentJSON;
import Artemis.Models.Weather.Daily;
import Artemis.Models.Weather.ForecastWeather;
import Artemis.Models.Weather.Weather;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXTabPane;
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
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.controlsfx.control.PopOver;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class StudentDashboard extends Application implements Initializable {

    private static int userId;
    private String fullName = "";


    private final String ANNOUCEMENTS_PATH = "api/announcements";
    private final String MARKS_PATH = "api/students/" + String.valueOf(userId) + "/marks";
    private final String SUBJECTS_PATH = "api/students/" + String.valueOf(userId) + "/classes";
    private final String WEATHER_PATH = "api/weather";
    private final String STUDENT_LIST_PATH = "api/students";
    private final String QUOTE_PATH = "api/quote";

    private static String accessToken;

    final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

    private boolean weatherPanePrepared = false;
    private boolean marksPanePrepared = false;
    private boolean subjectsPanePrepared = false;
    private boolean disciplinePanePrepared = false;

    @FXML
    Tab formTab1 = new Tab();
    @FXML
    Tab formTab2 = new Tab();
    @FXML
    Tab formTab3 = new Tab();
    @FXML
    Tab formTab4 = new Tab();
    @FXML
    Tab formTab5 = new Tab();
    @FXML
    Tab formTab6 = new Tab();



    //The "daily motivation" quote on the home pane
    @FXML
    Text quoteText = new Text();
    @FXML
    Text lblNoMarks = new Text();

    @FXML
    MFXTableView<Dots> dotsTable = new MFXTableView<>();

    //StackPane config
    @FXML
    StackPane stackPane = new StackPane();
    @FXML
    Pane homePane = new Pane();
    @FXML
    Pane marksPane = new Pane();
    @FXML
    Pane subjectsPane = new Pane();
    @FXML
    Pane disciplinePane = new Pane();
    @FXML
    Pane weatherPane = new Pane();
    @FXML
    Label lblDate = new Label();
    @FXML
    Label timeLabel = new Label();
    @FXML
    TableView<Announcement> announcementTable;
    @FXML
    TableColumn<Announcement, String> subject;
    @FXML
    Label lblFullName = new Label();
    @FXML
    Label lblForm = new Label();
    @FXML
    Label welcomeBack = new Label();
    @FXML
    Button signOut = new Button();
    @FXML
    Button settings = new Button();


    //Marks Pane components
    @FXML
    JFXTabPane formTabPane = new JFXTabPane();

    //Settings Popover components

    PopOver popOver;

    @FXML
    MFXTableView<Classes> classesTableView = new MFXTableView<>();

    //Weather Pane Labels - Today
    @FXML
    Text todayHigh = new Text();
    @FXML
    Text todayLow = new Text();
    @FXML
    Text todayWind = new Text();
    @FXML
    Text todayConditions = new Text();

    //Weather Pane Labels - Second Day
    @FXML
    Text secondDayHigh = new Text();
    @FXML
    Text secondDayLow = new Text();
    @FXML
    Text secondDayConditions = new Text();

    //Weather Pane Labels - Third Day
    @FXML
    Text thirdDayHigh = new Text();
    @FXML
    Text thirdDayLow = new Text();
    @FXML
    Text thirdDayConditions = new Text();

    //Weather Pane Labels - Fourth Day
    @FXML
    Text fourthDayHigh = new Text();
    @FXML
    Text fourthDayLow = new Text();
    @FXML
    Text fourthDayConditions = new Text();

    //Weather Pane Labels - Fifth Day
    @FXML
    Text fifthDayHigh = new Text();
    @FXML
    Text fifthDayLow = new Text();
    @FXML
    Text fifthDayConditions = new Text();

    //Weather Pane Labels - Six Day
    @FXML
    Text sixthDayHigh = new Text();
    @FXML
    Text sixthDayLow = new Text();
    @FXML
    Text sixthDayConditions = new Text();

    //Weather Pane Labels - Seventh Day
    @FXML
    Text seventhDayHigh = new Text();
    @FXML
    Text seventhDayLow = new Text();
    @FXML
    Text seventhDayConditions = new Text();

    //Weather Pane Images
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

    ForecastWeather forecastWeather = null;
    int currentForm;


    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException, JSONException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { //for announcements table

        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        ObservableList<Announcement> obsListAnnouncements = null;
        try {
            obsListAnnouncements = prepareHomePane();
            Gson gson = new Gson();
            forecastWeather = gson.fromJson(fetchWeatherData(), ForecastWeather.class);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        popOver = new PopOver();

        AnchorPane ap = null;
        try {
            ap = FXMLLoader.load(getClass().getResource("/SettingsPopover.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        popOver.setContentNode(ap);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);


        try { //Handler for if there are no announcements
            announcementTable.getItems().setAll(obsListAnnouncements);
        }catch (NullPointerException n){

        }
        initStackPane();

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
                lblDate.setText(dayString + " " + day + " " + date.getMonth());
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        //-------------------------------------------------------------------------------------------
    }

    private String performHttpGet(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try{
            CloseableHttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }

        catch(ConnectException e){
            alert.setContentText("Error connecting to server");
            alert.showAndWait();
        }

        return null;
    }

    private String performHttpPost(String url) throws  IOException{

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
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

    private ObservableList<Announcement> prepareHomePane() throws IOException{

        Gson gson = new Gson();
        String currentStudentJSON = performHttpGet(App.BASEURL + STUDENT_LIST_PATH + "/" + String.valueOf(userId));
        StudentJSON[] currentStudentInfo = gson.fromJson(currentStudentJSON, StudentJSON[].class);
        lblForm.setText("FORM " + String.valueOf(currentStudentInfo[0].getForm()));
        lblFullName.setText(currentStudentInfo[0].getUser_details().get("first_name") + " " + currentStudentInfo[0].getUser_details().get("last_name"));

        String quoteResponseBody = performHttpGet(App.BASEURL + QUOTE_PATH);

        Quote dailyQuote = gson.fromJson(quoteResponseBody, Quote.class);
        quoteText.setText(dailyQuote.getQuote() + " - " + dailyQuote.getAuthor());

        //Get announcements list
        String announcementsResponseBody = performHttpGet(App.BASEURL + ANNOUCEMENTS_PATH);

            Type announcementListType = new TypeToken<ArrayList<Announcement>>() {
            }.getType();
            ArrayList<Announcement> announcements = gson.fromJson(announcementsResponseBody, announcementListType);

        //table only accepts observable list, so the arraylist needs to be converted to an observable list like so...
        return FXCollections.observableArrayList(announcements);
    }

    private int fetchDayOfWeek(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    private String fetchDayOfWeekString(LocalDate date){
        DayOfWeek day = date.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private void initStackPane(){
        stackPane.getChildren().clear();
        stackPane.getChildren().add(homePane);
    }

    @FXML
    private void signOutActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        String response = performHttpPost("https://artemisystem.xyz/api/auth/logout");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Logged out successfully");
        alert.showAndWait();

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




    @FXML
    private void settingsActionPerformed(ActionEvent event){
        event.consume();
        popOver.show(settings);
    }

    @FXML
    private void homeActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(homePane);
    }
    @FXML
    private void marksActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(marksPane);
        prepareMarksPane();
    }
    @FXML
    private void subjectsActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(subjectsPane);
        prepareSubjectsPane();

    }
    @FXML
    private void disciplineActionPerformed(ActionEvent event) throws IOException {
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(disciplinePane);
        prepareDisciplinePane();
    }
    @FXML
    private void weatherActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(weatherPane);
        prepareWeatherPane();
    }

    private void prepareMarksPane() throws IOException {

        /*
        For reference, a sample JSON "marks" object which is basically one mark for a specific assignment

        [
    {
        "id": 1,
        "assignment": {
            "id": 1,
            "assignment_name": "Atom Naming",
            "max_marks": "48.0",
            "date_assigned": "2020-08-08",
            "date_due": "2020-08-09",
            "teacher": {
                "user_id": 21,
                "subject": "Mathematics"
            }
        },
        "mark_awarded": "47.5",
        "class_id": 3
    }
]
         */


        if (!marksPanePrepared) {
            boolean marksEmpty = false;
            String response = performHttpGet(App.BASEURL + MARKS_PATH);

            if (response.equals("[]")) { //Handling a case where the student has no marks, and thus no tabs
                marksEmpty = true;
            }

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    //registering this type adapter allows the date to be deserialized into a LocalDate instead of a Date, preventing a runtime exception
                    .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>(){
                        @Override
                        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                        }
            }).create();

            Marks[] studentMarks = gson.fromJson(response, Marks[].class);

            if (!marksEmpty) {
                for (Marks mark : studentMarks) {
                    mark.initialize();
                }

            //First add the required year and subjects per year tabs to a hashmap
            HashMap<Integer, List<String>> yearAndSubjectTabsRequired = new HashMap<>();
            for (int i = 0; i < studentMarks.length; i++) {

                LocalDate dueDate = studentMarks[i].getAssignment().getDateDue();
                int year = dueDate.getYear();

                String currentSubject = studentMarks[i].getAssignment().getTeacher().getSubject();

                if (!yearAndSubjectTabsRequired.containsKey(year)) {
                    List<String> subjectToBeAdded = new ArrayList<>();
                    subjectToBeAdded.add(currentSubject);
                    yearAndSubjectTabsRequired.put(year, subjectToBeAdded);
                } else if (yearAndSubjectTabsRequired.containsKey(year)) {
                    if (!yearAndSubjectTabsRequired.get(year).contains(currentSubject)) {
                        List<String> amendedSubjectsList = yearAndSubjectTabsRequired.get(year);
                        amendedSubjectsList.add(currentSubject);
                        yearAndSubjectTabsRequired.put(year, amendedSubjectsList);
                    }
                }
            }

            //Now read from the hashmap and add the required tabs to the window
            for (Map.Entry<Integer, List<String>> entry : yearAndSubjectTabsRequired.entrySet()) {
                int year = entry.getKey();
                Tab yearTab = new Tab(String.valueOf(year));
                formTabPane.getTabs().add(yearTab);

                List<String> subjects = entry.getValue();
                JFXTabPane subjectsTabPane = new JFXTabPane();
                yearTab.setContent(subjectsTabPane);

                for (String subject : subjects) {
                    Tab subjectTab = new Tab(subject);
                    AnchorPane background = new AnchorPane();

                    MFXTableView<Marks> marksTableView = new MFXTableView<>();
                    MFXTableColumn<Marks> colTask = new MFXTableColumn<>("Task", Comparator.comparing(Marks::getAssignmentName));
                    MFXTableColumn<Marks> colMark = new MFXTableColumn<>("Mark", Comparator.comparing(Marks::getMarkAwardedStringRepresentation));
                    MFXTableColumn<Marks> colPercentage = new MFXTableColumn<>("Percentage", Comparator.comparing(Marks::getPercentage));

                    colTask.setRowCellFunction(marks -> new MFXTableRowCell(marks.getAssignmentName()));
                    colMark.setRowCellFunction(marks -> new MFXTableRowCell(marks.getMarkAwardedStringRepresentation()));
                    colPercentage.setRowCellFunction(marks -> new MFXTableRowCell(marks.getPercentage()));
                    marksTableView.getTableColumns().addAll(colTask, colMark, colPercentage);

                    background.getChildren().add(marksTableView);
                    subjectTab.setContent(background);
                    subjectsTabPane.getTabs().add(subjectTab);
                }
            }

            //Finally, iterate through the student marks and add them to the correct tables/tabs
            for (Marks mark : studentMarks) {
                LocalDate dateDue = mark.getAssignment().getDateDue();
                String subject = mark.getAssignment().getTeacher().getSubject();


                int year = dateDue.getYear();

                for (int a = 0; a < formTabPane.getTabs().size(); a++) {
                    Tab currentTab = formTabPane.getTabs().get(a);
                    if (currentTab.getText().equals(String.valueOf(year))) {
                        JFXTabPane subjectTabPane = (JFXTabPane) currentTab.getContent();
                        for (int i = 0; i < subjectTabPane.getTabs().size(); i++) {
                            Tab subjectTab = subjectTabPane.getTabs().get(i);
                            if (subjectTab.getText().equals(subject)) {
                                AnchorPane ap = (AnchorPane) subjectTab.getContent();
                                MFXTableView marksTable = (MFXTableView) ap.getChildren().get(0);

                                //After making amendments to the data, update the GUI components
                                marksTable.getItems().add(mark);
                                ap.getChildren().set(0, marksTable);
                                subjectTab.setContent(ap);
                                subjectTabPane.getTabs().set(i, subjectTab);
                                currentTab.setContent(subjectTabPane);
                                formTabPane.getTabs().set(a, currentTab);
                            }
                        }
                    }
                }
            }
        }
            else {
                lblNoMarks.setText("You have no marks");
            }
        }
        marksPanePrepared = true;
    }

    private void prepareSubjectsPane() throws IOException {

        if(!subjectsPanePrepared){
            String response = performHttpGet(App.BASEURL + SUBJECTS_PATH);
            Gson gson = new Gson();
            Classes[] classesArray = gson.fromJson(response, Classes[].class);

            List<Classes> classesList = Arrays.asList(classesArray);
            ObservableList classesObservableList = FXCollections.observableList(classesList);

            MFXTableColumn<Classes> colID = new MFXTableColumn<>("ID", Comparator.comparing(Classes::getId));
            MFXTableColumn<Classes> colName = new MFXTableColumn<>("Name", Comparator.comparing(Classes::getName));

            colID.setRowCellFunction(classes -> new MFXTableRowCell(String.valueOf(classes.getId())));
            colName.setRowCellFunction(classes -> new MFXTableRowCell(classes.getName()));

            classesTableView.setItems(classesObservableList);
            classesTableView.getTableColumns().addAll(colID, colName);
            subjectsPanePrepared = true;

        }
    }

    private void prepareDisciplinePane() throws IOException {

        if(!disciplinePanePrepared){
            String response = performHttpGet(App.BASEURL + STUDENT_LIST_PATH + "/" + String.valueOf(userId) + "/dots");
            Gson gson = new Gson();
            Dots[] dotsArray = gson.fromJson(response, Dots[].class);

            for(Dots dot : dotsArray){
                dot.initialize();
            }

            List<Dots> dotsList = Arrays.asList(dotsArray);
            ObservableList dotsObservableList = FXCollections.observableList(dotsList);

            MFXTableColumn<Dots> colID = new MFXTableColumn<>("ID", Comparator.comparing(Dots::getId));
            MFXTableColumn<Dots> colReason = new MFXTableColumn<>("Reason", Comparator.comparing(Dots::getReason));
            MFXTableColumn<Dots> colGivenBy = new MFXTableColumn<>("Assigning teacher ID", Comparator.comparing(Dots::getAssigningTeacherId));

            colID.setRowCellFunction(dots -> new MFXTableRowCell(String.valueOf(dots.getId())));
            colReason.setRowCellFunction(dots -> new MFXTableRowCell(dots.getReason()));
            colGivenBy.setRowCellFunction(dots -> new MFXTableRowCell(String.valueOf(dots.getAssigningTeacherId())));

            dotsTable.setItems(dotsObservableList);
            dotsTable.getTableColumns().addAll(colID, colReason, colGivenBy);
            dotsTable.setHeaderText("Total dots this week: " + dotsTable.getItems().size());
            disciplinePanePrepared = true;

        }
    }


    private String fetchWeatherData() throws IOException, JSONException {
        String forecastWeatherData = performHttpGet(App.BASEURL + WEATHER_PATH);
    return forecastWeatherData;
    }

    private void prepareWeatherPane(){

        if(!weatherPanePrepared) { //Saves us from wasting CPU time by not re-preparing the weather pane
            String latitude = forecastWeather.getLatitude();
            String longitude = forecastWeather.getLongitude();
            Daily[] dailyWeather = forecastWeather.getDaily();
            LocalDate date = LocalDate.now();
            String today = fetchDayOfWeekString(date);

            //Wind data is only needed for the current day
            long windDegrees = Math.round(dailyWeather[0].getWind_deg());
            long windSpeed = Math.round(dailyWeather[0].getWind_speed());

            setWeatherLabels(dailyWeather);
            setWeatherImages(dailyWeather);
            weatherPanePrepared = true;
        }
    }

    //Sets the text labels of temperatures, days etc on the weatherPane
    private void setWeatherLabels(Daily[] dailyWeather){
        todayLow.setText(Math.round(dailyWeather[0].getTemp().getMin()) + "\u00B0C"); // \u00B0 is the unicode degree symbol
        todayHigh.setText(Math.round(dailyWeather[0].getTemp().getMax()) + "\u00B0C");
        Weather[] weather = dailyWeather[0].getWeather();
        todayConditions.setText(weather[0].getDescription());

        String windDirection = determineWindDirection(Math.round(dailyWeather[0].getWind_deg()));
        todayWind.setText(dailyWeather[0].getWind_speed() + " Km/h " + windDirection);

        secondDayLow.setText(Math.round(dailyWeather[1].getTemp().getMax()) + "\u00B0C");
        secondDayHigh.setText(Math.round(dailyWeather[1].getTemp().getMax()) + "\u00B0C");
        Weather[] weather2 = dailyWeather[1].getWeather();
        secondDayConditions.setText(weather2[0].getDescription());

        thirdDayLow.setText(Math.round(dailyWeather[2].getTemp().getMin()) + "\u00B0C");
        thirdDayHigh.setText(Math.round(dailyWeather[2].getTemp().getMax()) + "\u00B0C");
        Weather[] weather3 = dailyWeather[2].getWeather();
        thirdDayConditions.setText(weather3[0].getDescription());

        fourthDayLow.setText(Math.round(dailyWeather[3].getTemp().getMin()) + "\u00B0C");
        fourthDayHigh.setText(Math.round(dailyWeather[3].getTemp().getMax()) + "\u00B0C");
        Weather[] weather4 = dailyWeather[3].getWeather();
        fourthDayConditions.setText(weather4[0].getDescription());

        fifthDayLow.setText(Math.round(dailyWeather[4].getTemp().getMin()) + "\u00B0C");
        fifthDayHigh.setText(Math.round(dailyWeather[4].getTemp().getMax()) + "\u00B0C");
        Weather[] weather5 = dailyWeather[4].getWeather();
        fifthDayConditions.setText(weather5[0].getDescription());

        sixthDayLow.setText(Math.round(dailyWeather[5].getTemp().getMin()) + "\u00B0C");
        sixthDayHigh.setText(Math.round(dailyWeather[5].getTemp().getMax()) + "\u00B0C");
        Weather[] weather6 = dailyWeather[5].getWeather();
        sixthDayConditions.setText(weather6[0].getDescription());

        seventhDayLow.setText(Math.round(dailyWeather[6].getTemp().getMin()) + "\u00B0C");
        seventhDayHigh.setText(Math.round(dailyWeather[6].getTemp().getMax()) + "\u00B0C");
        Weather[] weather7 = dailyWeather[6].getWeather();
        seventhDayConditions.setText(weather7[0].getDescription());

    }

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

    /*
      This method determines which weather icon should be used based on the day's weather. For more information
      on these weather codes, see https://openweathermap.org/weather-conditions
    */
    private Image determineWeatherImages(int id){

        if(id >= 200 && id <= 232){ // id  200 - 232 = Thunderstorm
            return new Image(App.class.getResourceAsStream("/Images/Weather/11d@2x.png"));
        }
        else if(id >= 300 && id <= 321){ //id 300 - 321 = Drizzle
            return new Image(App.class.getResourceAsStream("/Images/Weather/09d@2x.png"));
        }
        else if(id >= 500 && id <= 504){ //id 500 - 504 = Normal rain
            return new Image(App.class.getResourceAsStream("/Images/Weather/10d@2x.png"));
        }
        else if(id == 511){ //id 511 = Freezing rain (Rare  "corner case")
            return new Image(App.class.getResourceAsStream("/Images/Weather/13d@2x.png"));
        }
        else if(id >= 520 && id <= 531){ //id 520 - 531 = Shower rain
            return new Image(App.class.getResourceAsStream("/Images/Weather/09d@2x.png"));
        }
        else if(id >= 600 && id <= 622){ //id 600 - 622 = Snow
            return new Image(App.class.getResourceAsStream("/Images/Weather/13d@2x.png"));
        }
        else if(id >= 701 && id <= 781){ //id 701 - 781 = Atmosphere (like mist, or ash)
            return new Image(App.class.getResourceAsStream("/Images/Weather/50d@2x.png"));
        }
        else if(id == 800){ //id 800 = Clear sky
            return new Image(App.class.getResourceAsStream("/Images/Weather/01d@2x.png"));
        }
        else if(id == 801){ //id 801 = Few clouds
            return new Image(App.class.getResourceAsStream("/Images/Weather/02d@2x.png"));
        }
        else if(id == 802){ //id 802 = Scattered clouds
            return new Image(App.class.getResourceAsStream("/Images/Weather/03d@2x.png"));
        }
        else if(id == 803 || id == 804){ //id 803 - 804 = Broken and Overcast clouds
            return new Image(App.class.getResourceAsStream("/Images/Weather/04d@2x.png"));
        }
        return null;
    }

    private String determineWindDirection(long windDegrees){ //For "today's" weather only

        if(windDegrees >= 0  && windDegrees < 45){ //Not sure if the weatherAPI returns 0 deg or 360 deg
            return "North";
        }
        else if(windDegrees >= 45 && windDegrees < 90){
            return "North East";
        }
        else if(windDegrees >= 90 && windDegrees < 135){
            return "East";
        }
        else if(windDegrees >= 135 && windDegrees < 180){
            return "South East";
        }
        else if(windDegrees >= 180 & windDegrees < 225){
            return "South";
        }
        else if(windDegrees >= 225 && windDegrees < 270){
            return "South West";
        }
        else if(windDegrees >= 270 && windDegrees < 315){
            return "West";
        }
        else if(windDegrees >= 315 && windDegrees < 359) {
            return "North West";
        }
        return null;
    }


    private int determineNextDay(int additionalDays){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, additionalDays);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Label getLblDate() {
        return lblDate;
    }

    public void setLblDate(String dd) {
        lblDate.setText(dd);
    }


    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        StudentDashboard.userId = userId;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        StudentDashboard.accessToken = accessToken;
    }

    public void setCurrentForm(int currentForm) {
        this.currentForm = currentForm;
    }
}

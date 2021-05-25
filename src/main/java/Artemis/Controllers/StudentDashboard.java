package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.Announcement;
import Artemis.Models.Weather.Daily;
import Artemis.Models.Weather.ForecastWeather;
import Artemis.Models.Weather.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXTabPane;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kordamp.bootstrapfx.scene.layout.Panel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

public class StudentDashboard extends Application implements Initializable {

    private static String accessToken;

    private String fullName = "";

    private static int userId;

    final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

    private boolean weatherPanePrepared = false;


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
    Panel alertPanel = new Panel();
    @FXML
    Label dateLabel = new Label();
    @FXML
    Label timeLabel = new Label();
    @FXML
    TableView<Announcement> announcementTable;
    @FXML
    TableColumn<Announcement, String> subject;
    @FXML
    Label fullNameText = new Label();
    @FXML
    Label welcomeBack = new Label();
    @FXML
    JFXTabPane marksTabPane = new JFXTabPane();

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
        announcementTable.getItems().setAll(obsListAnnouncements);//TODO Add handler for if announcements = null
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
                dateLabel.setText(dayString + " " + day + " " + date.getMonth());
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        try{
            CloseableHttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
        catch(ConnectException e){
            alert.setContentText("Error connecting to server");
            alert.showAndWait();
        }
        catch (Exception f){
            f.printStackTrace();
        }
        return null;
    }

    private ObservableList<Announcement> prepareHomePane() throws IOException, JSONException {


        String studentResponseBody = performHttpGet("https://artemisystem.xyz/api/student/home/" + userId);
        JSONObject nameObj = new JSONObject(studentResponseBody);
        String firstName = (nameObj.get("firstName")).toString();
        String lastName = (nameObj.get("lastName").toString());
        fullNameText.setText(firstName + " " + lastName);
        welcomeBack.setText("Welcome back " + firstName);

        String announcementsResponseBody = performHttpGet("https://artemisystem.xyz/api/announcements");

            Gson gson = new Gson();

            Type announcementListType = new TypeToken<ArrayList<Announcement>>(){}.getType();
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
    private void homeActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(homePane);
    }
    @FXML
    private void marksActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(marksPane);
    }
    @FXML
    private void subjectsActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(subjectsPane);
    }
    @FXML
    private void disciplineActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(disciplinePane);
    }
    @FXML
    private void weatherActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(weatherPane);
        prepareWeatherPane();
    }

    private String fetchWeatherData() throws IOException, JSONException {
        String forecastWeatherData = performHttpGet("https://artemisystem.xyz/api/weather");
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
    //See https://openweathermap.org/weather-conditions for more information
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

    public Label getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dd) {
        dateLabel.setText(dd);
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
}

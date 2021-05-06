package Artemis.Controllers;

import Artemis.Models.Announcement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
import java.util.*;

public class StudentDashboard extends Application implements Initializable {

    private static String accessToken;

    private String fullName = "";

    private static int userId;

    final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");




    //StackPane config
    @FXML
    Pane stackPane = new StackPane();
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
    Label timeLabel = new Label();
    @FXML
    TableView<Announcement> announcementTable;
    @FXML
    TableColumn<Announcement, String> subject;
    @FXML
    Label fullNameText = new Label();


    Pane[] paneArr = {homePane,marksPane,subjectsPane,disciplinePane,weatherPane};
    

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException, JSONException {








    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { //for announcements table
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        ObservableList<Announcement> oannouncements = null;
        try {
            oannouncements = prepareHomePane();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

            announcementTable.getItems().setAll(oannouncements);


        //---------------------UPDATING DASHBOARD TIME EVERY SECOND ON ANOTHER THREAD-------------------------

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler() {
        //event.consume() is never called, so the event keeps looping over and over every second
            @Override
            public void handle(Event event) {
                Calendar cal = Calendar.getInstance();
                timeLabel.setText(timeFormat.format(cal.getTime()));
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
        try{
            CloseableHttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
        catch(ConnectException e){
            e.printStackTrace();
        }
        return null;
    }

    private ObservableList<Announcement> prepareHomePane() throws IOException, JSONException {


        String studentResponseBody = performHttpGet("http://127.0.0.1:5000/api/student/home/" + userId);
        JSONObject nameObj = new JSONObject(studentResponseBody);
        String firstName = (nameObj.get("FirstName")).toString();
        String lastName = (nameObj.get("LastName").toString());
        fullNameText.setText(firstName + " " + lastName);


        String announcementsResponseBody = performHttpGet("http://127.0.0.1:5000/api/announcements");

            Gson gson = new Gson();

            Type announcementListType = new TypeToken<ArrayList<Announcement>>(){}.getType();
            ArrayList<Announcement> announcements = gson.fromJson(announcementsResponseBody, announcementListType);

        //table only accepts observable list, so the arraylist needs to be converted to an observable list like so...
        return FXCollections.observableArrayList(announcements);
    }




    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String dd) {
        timeLabel.setText(dd);
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

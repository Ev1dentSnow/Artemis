package Artemis.Controllers;

import Artemis.App;
import com.calendarfx.view.YearMonthView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import javafx.scene.control.Label;
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

public class AdminDashboard extends Application implements Initializable {


    private static String accessToken;

    private static int userId;

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
    private void studentsActionPerformed(ActionEvent event){
        event.consume();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(studentsPane);
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
}

package Artemis.Controllers;

import Artemis.Controllers.LoginController;
import Artemis.Models.Announcement;
import Artemis.Models.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.bootstrapfx.scene.layout.Panel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class StudentDashboard extends Application implements Initializable {

    private String accessToken = "";

    private String fullName = "";

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
    private Text dayDate = new Text();
    @FXML
    private TableView<Announcement> announcementTable;
    @FXML
    private TableColumn<Announcement, String> subject;


    Pane[] paneArr = {homePane,marksPane,subjectsPane,disciplinePane,weatherPane};
    

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException{
        //Load FXML file and display window
        Parent root = FXMLLoader.load(getClass().getResource("/StudentDashboard.fxml"));


        //alertPanel.getStyleClass().setAll("alert","alert-success");



        Scene scene = new Scene(root,1100,650);
        //scene.getStylesheets().add("LightMode.css");
        scene.getStylesheets().setAll(BootstrapFX.bootstrapFXStylesheet()); //for bootstrapFX compatibility
        //hidePreviousPane(homePane);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.prepareHomePane();
        //---------------------UPDATING DASHBOARD TIME EVERY 5 SECONDS ON ANOTHER THREAD-------------------------
        Timer timer = new Timer();
        TimerTask updateTime = new TimerTask() {
            @Override
            public void run() {
                setDateAndTime();
            }
        };

        timer.schedule(updateTime, 5000, 5000);
        //----------------------------------------------------------------------




    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { //for announcements table
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
    }

    private void prepareHomePane() throws IOException {




        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://127.0.0.1:5000/api/student/home");
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        try{
            CloseableHttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            Gson gson = new Gson();

            Type announcementListType = new TypeToken<ArrayList<Announcement>>(){}.getType();
            ArrayList<Announcement> announcements = gson.fromJson(responseBody, announcementListType);
            announcementTable.getItems().setAll(announcements);


        }
        catch(ConnectException e){
            e.printStackTrace();
        }
    }

    private void setDateAndTime(){

        String day = LocalDate.now().getDayOfWeek().name();



        DateFormat dateFormat = new SimpleDateFormat("dd/MM");
        Date date = new Date();
        String dateString = dateFormat.format(date);


        this.setDayDate(day);

    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Text getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dd) {
        dayDate.setText(dd);
    }
}

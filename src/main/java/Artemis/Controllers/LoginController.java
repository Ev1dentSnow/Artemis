package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.Student;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController extends Application {

    private static final String LOCAL_LOGIN_URL = "http://127.0.0.1:5000/auth/login";
    private static final String ONLINE_LOGIN_URL = "https://artemisystem.xyz/api/auth/login";
    private boolean keyTyped = true;
    @FXML
    TextField txfUsername = new TextField();
    @FXML
    PasswordField txfPassword = new PasswordField();
    @FXML
    Rectangle loginRectangle = new Rectangle();
    @FXML
    Rectangle passwordRectangle = new Rectangle();
    @FXML
    Label notificationLabel = new Label();

    private String accessToken = "";
    private String permissionLevel = "";
    private int userId = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

    }

    @FXML
    private void submitLoginDetails(ActionEvent event) throws IOException, JSONException {
        event.consume();
        keyTyped = false;
        String username = txfUsername.getText();
        String password = txfPassword.getText();

        if(!username.equals("") && !password.equals("")) {

            int responseStatusCode = 0;

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(ONLINE_LOGIN_URL);

            List<NameValuePair> body = new ArrayList<NameValuePair>();
            body.add(new BasicNameValuePair("username", username));
            body.add(new BasicNameValuePair("password", password));
            httpPost.setEntity(new UrlEncodedFormEntity(body));


            try {
                CloseableHttpResponse response = httpClient.execute(httpPost);
                responseStatusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());


                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                if (responseStatusCode == 200) {
                    alert.setContentText("Login Successful");
                    alert.showAndWait();

                    JSONObject responseObj = new JSONObject(responseBody);
                    String responseToken = (String) responseObj.get("access_token");
                    String permissionLevel = (String) responseObj.get("role");
                    int userId = (Integer) responseObj.get("userId");

                    this.setAccessToken(responseToken);
                    this.setPermissionLevel(permissionLevel);
                    this.setUserId(userId);


                    if(permissionLevel.equals("student")){
                       loadStudentDashboard(event);
                    }
                    else if(permissionLevel.equals("teacher")){
                        loadTeacherDashboard(event);
                    }
                    else if(permissionLevel.equals("admin")){
                        loadAdminDashboard(event);
                    }

                } else {
                    if(responseStatusCode == 401){
                        displayInvalidCredentialsNotification();
                    }
                    else{
                        alert.setContentText("Login Failed, HTTP STATUS CODE: " + responseStatusCode);
                        alert.showAndWait();
                    }
                }

            } catch (ConnectException | InterruptedException e) {

                Alert connError = new Alert(Alert.AlertType.ERROR);
                connError.setContentText("Error connecting to server");
                connError.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please enter both a username and a password");
            alert.showAndWait();
        }

    }

    @FXML
    private void forgotPassword(ActionEvent event){
        event.consume();
        Alert forgotAlert = new Alert(Alert.AlertType.INFORMATION);
        forgotAlert.setContentText("Please contact a system administrator to reset your password for you");
        forgotAlert.showAndWait();
    }

    private void loadStudentDashboard(ActionEvent event) throws IOException {
        StudentDashboard.setAccessToken(accessToken);
        StudentDashboard.setUserId(userId);
        AnchorPane sd = (AnchorPane) FXMLLoader.load(getClass().getResource("/StudentDashboard.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.getIcons().add(new Image(App.class.getResourceAsStream("/fxmlAssets/ArtemisAlpha.png")));
        window.setTitle("Artemis");
        window.setScene(new Scene(sd));
        sd.getStylesheets().add("TabbedPanes.css");
        window.setResizable(false);
        window.setWidth(1100);
        window.setHeight(680);
        window.show();
    }

    private void loadTeacherDashboard(ActionEvent event){

    }

    private void loadAdminDashboard(ActionEvent event) throws IOException {
        AdminDashboard.setAccessToken(accessToken);
        AdminDashboard.setUserId(userId);
        AnchorPane AdminDashboard = (AnchorPane) FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.getIcons().add(new Image(App.class.getResourceAsStream("/fxmlAssets/ArtemisAlpha.png")));
        window.setTitle("Artemis");
        window.setScene(new Scene(AdminDashboard));
        window.setResizable(false);
        window.show();
    }

    private void displayInvalidCredentialsNotification() throws InterruptedException {
        FillTransition usernameTransition = new FillTransition(Duration.millis(500), loginRectangle, Color.WHITE, Color.RED);
        usernameTransition.setAutoReverse(true);
        usernameTransition.setCycleCount(1);
        FillTransition passwordTransition = new FillTransition(Duration.millis(500), passwordRectangle, Color.WHITE, Color.RED);
        passwordTransition.setAutoReverse(true);
        passwordTransition.setCycleCount(1); //once cycle to go red, one cycle to go back to white

        usernameTransition.play();
        passwordTransition.play();
        notificationLabel.setTextFill(Color.RED);

    }

    @FXML
    private void usernameKeyTyped(KeyEvent event){
        event.consume();
        if(!keyTyped) {
            FillTransition usernameTransition = new FillTransition(Duration.millis(500), loginRectangle, Color.RED, Color.WHITE);
            usernameTransition.setAutoReverse(true);
            usernameTransition.setCycleCount(1);
            FillTransition passwordTransition = new FillTransition(Duration.millis(500), passwordRectangle, Color.RED, Color.WHITE);
            passwordTransition.setAutoReverse(true);
            passwordTransition.setCycleCount(1);
            usernameTransition.play();
            passwordTransition.play();
            keyTyped = true;
            notificationLabel.setTextFill(Color.BLACK);
        }
    }

    @FXML
    private void passwordKeyTyped(KeyEvent event){
        event.consume();
        if(!keyTyped) {
            FillTransition usernameTransition = new FillTransition(Duration.millis(500), loginRectangle, Color.RED, Color.WHITE);
            usernameTransition.setAutoReverse(true);
            usernameTransition.setCycleCount(1);
            FillTransition passwordTransition = new FillTransition(Duration.millis(500), passwordRectangle, Color.RED, Color.WHITE);
            passwordTransition.setAutoReverse(true);
            passwordTransition.setCycleCount(1);
            usernameTransition.play();
            passwordTransition.play();
            keyTyped = true;
            notificationLabel.setTextFill(Color.BLACK);
        }
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String responseToken) {
        accessToken = responseToken;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
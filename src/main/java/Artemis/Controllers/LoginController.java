package Artemis.Controllers;

import Artemis.App;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.controls.MFXNotification;
import io.github.palexdev.materialfx.controls.SimpleMFXNotificationPane;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import io.github.palexdev.materialfx.notifications.NotificationPos;
import io.github.palexdev.materialfx.notifications.NotificationsManager;
import javafx.animation.FillTransition;
import javafx.application.Application;
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
import javafx.scene.layout.Region;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


public class LoginController extends Application {

    private static final String LOGIN_PATH = "api/authentication";

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
            HttpPost httpPost = new HttpPost(App.BASEURL + LOGIN_PATH);

            List<NameValuePair> body = new ArrayList<NameValuePair>();
            body.add(new BasicNameValuePair("username", username));
            body.add(new BasicNameValuePair("password", password));
            httpPost.setEntity(new UrlEncodedFormEntity(body));


            try {
                CloseableHttpResponse response = httpClient.execute(httpPost);
                responseStatusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                Alert selectLoginTypeAlert = new Alert(Alert.AlertType.INFORMATION);

                if (responseStatusCode == 200) {

                    MFXNotification notification = buildNotification();
                    NotificationsManager.send(NotificationPos.TOP_RIGHT, notification);

                    //alert.setContentText("Login Successful");
                    //alert.showAndWait();

                    JSONObject responseObj = new JSONObject(responseBody);
                    String responseToken = (String) responseObj.get("access");

                    DecodedJWT jwt = JWT.decode(responseToken);
                    String permissionLevel = jwt.getClaim("roles").toString();
                    userId = Integer.parseInt(jwt.getClaim("user_id").toString());


                    this.setAccessToken(responseToken);
                    this.setPermissionLevel(permissionLevel);
                    this.setUserId(userId);

                    //Determining which dashboard to open based on the user's role stated in the access token

                    if(permissionLevel.contains("student")){
                        StudentDashboard.setUserId(userId);
                       loadStudentDashboard(event);
                    }
                    else if(permissionLevel.contains("teacher")){
                        loadTeacherDashboard(event);
                    }
                    else if(permissionLevel.contains("admin")){
                        loadAdminDashboard(event);
                        AdminDashboard.setAccessToken(accessToken);
                    }
                    else if(permissionLevel.contains("teacher") && permissionLevel.contains("admin")) //Some teachers are admins too!
                        selectLoginTypeAlert.setContentText("What would you like to sign in as?");

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
        window.getIcons().add(new Image(App.class.getResourceAsStream("/Images/ArtemisAlpha.png")));
        window.setTitle("Artemis");
        window.setScene(new Scene(sd));
        sd.getStylesheets().add("CSS/TabbedPanes.css");
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
        window.getIcons().add(new Image(App.class.getResourceAsStream("/Images/ArtemisAlpha.png")));
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

    private MFXNotification buildNotification() {
        FontIcon icon = new FontIcon("fa-check-circle");
        icon.setIconColor(Color.LIGHTBLUE);
        icon.setIconSize(15);
        SimpleMFXNotificationPane pane = new SimpleMFXNotificationPane(icon, "Artemis School Management System", "Login Successful", "Welcome back!");
        Region template = pane;

        MFXNotification notification = new MFXNotification(template, true, true);
        notification.setHideAfterDuration(Duration.seconds(3));

        SimpleMFXNotificationPane notiPane = (SimpleMFXNotificationPane) template;
        notiPane.setCloseHandler(closeEvent -> notification.hideNotification());

        return notification;
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
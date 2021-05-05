package Artemis.Controllers;

import Artemis.Models.Student;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kordamp.bootstrapfx.BootstrapFX;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class LoginController extends Application {

    @FXML
    TextField txfUsername = new TextField();

    @FXML
    PasswordField txfPassword = new PasswordField();


    private String accessToken = "";
    private int permissionLevel = 0;
    private int userId = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

    }

    @FXML
    private void submitLoginDetails(ActionEvent event) throws UnsupportedEncodingException, IOException, JSONException {
        event.consume();
        String username = txfUsername.getText();
        String password = txfPassword.getText();

        if(!username.equals("") && !password.equals("")) {

            int responseStatusCode = 0;

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://127.0.0.1:5000/auth/login");

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

                    JSONObject tokenobj = new JSONObject(responseBody);
                    String responseToken = (String) tokenobj.get("access_token");
                    int permissionLevel = (Integer) tokenobj.get("role");
                    int userId = (Integer) tokenobj.get("user_id");

                    this.setAccessToken(responseToken);
                    this.setPermissionLevel(permissionLevel);
                    StudentDashboard.setUserId(userId);
                    StudentDashboard.setAccessToken(responseToken);

                    AnchorPane StudentDashboard = (AnchorPane) FXMLLoader.load(getClass().getResource("/StudentDashboard.fxml"));
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(new Scene(StudentDashboard));
                    StudentDashboard.getStylesheets().add("TabbedPanes.css");
                    window.setResizable(false);
                    window.setWidth(1100);
                    window.setHeight(680);
                    window.show();
                } else {

                    alert.setContentText("Login Failed, HTTP STATUS CODE: " + responseStatusCode);
                    alert.showAndWait();

                }

            } catch (ConnectException e) {

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


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String responseToken) {
        accessToken = responseToken;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
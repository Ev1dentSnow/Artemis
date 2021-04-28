package Artemis.Controllers;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginController extends Application {


    @FXML
    TextField txfUsername = new TextField();

    @FXML
    PasswordField txfPassword = new PasswordField();

    String[] userTypes = {"Student", "Teacher", "Administrator"};

    @FXML
    ComboBox choiceBox = new ComboBox(FXCollections.observableArrayList(userTypes));

    private String accessToken = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        AnchorPane root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
        root.getChildren().add(choiceBox);

        choiceBox.setLayoutX(520);
        choiceBox.setLayoutY(365);

        Scene scene = new Scene(root, 893,556);
        scene.getStylesheets().add("LoginViewStylesheet.css");

        primaryStage.setScene(scene);
        primaryStage.show();


    }

    @FXML
    private void submitLoginDetails(ActionEvent event) throws IOException, JSONException {
        event.consume();
        String username = txfUsername.getText();
        String password = txfPassword.getText();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:5000/auth/login");

        List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair("username", username));
        body.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(body));



        CloseableHttpResponse response = httpClient.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject tokenobj = new JSONObject(responseBody);
        String responseToken = (String) tokenobj.get("access_token");
        this.setAccessToken(responseToken);

    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String responseToken) {
        accessToken = responseToken;
    }
}
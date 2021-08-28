package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.Announcement;
import com.google.gson.Gson;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;

public class PublishNewAnnouncement extends Application {

    private static String accessToken;
    private final String ANNOUCEMENTS_PATH = "api/announcements/";
    CloseableHttpClient client = HttpClients.createDefault();

    @FXML
    private MFXTextField txfTitle;
    @FXML
    private TextArea txaContent;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    @FXML
    private void publishActionPerformed(ActionEvent event) throws IOException {

        String subject = txfTitle.getText();
        String content = txaContent.getText();
        Announcement newAnnouncement = new Announcement(0, subject, content);
        Gson gson = new Gson();
        String json = gson.toJson(newAnnouncement);

        CloseableHttpResponse response = performHttpPost(App.BASEURL + ANNOUCEMENTS_PATH, json);

        if(response.getStatusLine().getStatusCode() != 201){
            displayAlert("Error sending data to server", Alert.AlertType.ERROR);
        }

        else{
            displayAlert("Announcement published successfully", Alert.AlertType.INFORMATION);
            Stage currentWindow = (Stage) txfTitle.getScene().getWindow();
            currentWindow.close();
        }
    }

    @FXML
    private void cancelActionPerformed(ActionEvent event){
        Stage currentWindow = (Stage) txfTitle.getScene().getWindow();
        currentWindow.close();
    }


    /**
     * Executes an HTTP POST request to the supplied URL, and returns the response
     * @param url
     * @return CloseableHttpResponse
     * @throws IOException
     */

    private CloseableHttpResponse performHttpPost(String url, String jsonData) throws  IOException{

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

    private Optional<ButtonType> displayAlert(String content, Alert.AlertType alertType){
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

    public static void setAccessToken(String accessToken) {
        PublishNewAnnouncement.accessToken = accessToken;
    }
}

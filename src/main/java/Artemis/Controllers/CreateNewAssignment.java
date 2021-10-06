package Artemis.Controllers;

import Artemis.App;
import Artemis.Models.Assignment;
import Artemis.Models.Teacher;
import com.google.gson.*;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CreateNewAssignment extends Application {

    private static String accessToken;
    private static int teacherID;
    private static final String ASSIGNMENTS_PATH = "api/assignments/";
    CloseableHttpClient client = HttpClients.createDefault();

    @FXML
    private MFXTextField txfAssignmentName;
    @FXML
    private MFXTextField txfMaxMarks;
    @FXML
    private MFXDatePicker dateAssigned;
    @FXML
    private MFXDatePicker dateDue;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    @FXML
    private void createActionPerformed(ActionEvent event) {
        event.consume();

        String assignmentName = txfAssignmentName.getText();
        Double maxMarks = Double.parseDouble(txfMaxMarks.getText());
        LocalDate da = dateAssigned.getDate();
        LocalDate dd = dateDue.getDate();

        Assignment newAssignment = new Assignment();
        newAssignment.setAssignmentName(assignmentName);
        newAssignment.setMaxMarks(maxMarks);
        newAssignment.setDateDue(dd);
        newAssignment.setDateAssigned(da);
        Teacher teacher = new Teacher();
        teacher.setId(teacherID);
        newAssignment.setTeacher(teacher);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                //registering this type adapter allows the date to be serialized into a LocalDate instead of a Date, preventing a runtime exception
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
                    }
                })
                .create();
        String jsonBody = gson.toJson(newAssignment);

        CloseableHttpResponse response = null;
        try {
            response = performHttpPost(App.BASEURL + ASSIGNMENTS_PATH, jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.getStatusLine().getStatusCode() == 201) {
            displayAlert("Assignment created successfully!", Alert.AlertType.INFORMATION);
            Stage currentWindow = (Stage) txfAssignmentName.getScene().getWindow();
            currentWindow.close();
        }
        else {
            displayAlert("Error sending data to the server", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void cancelActionPerformed(ActionEvent event) {
        event.consume();
        Stage currentWindow = (Stage) txfAssignmentName.getScene().getWindow();
        currentWindow.close();
    }

    private CloseableHttpResponse performHttpPost(String url, String jsonData) throws IOException {

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

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        CreateNewAssignment.accessToken = accessToken;
    }

    public static int getTeacherID() {
        return teacherID;
    }

    public static void setTeacherID(int teacherID) {
        CreateNewAssignment.teacherID = teacherID;
    }
}

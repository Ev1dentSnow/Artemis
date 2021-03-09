package Artemis.ViewControllers;

import com.calendarfx.view.YearMonthView;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.io.IOException;

public class AdminDashboard extends Application {




    Image image = new Image(getClass().getResourceAsStream("/44_barack_obama.jpg"));;


    @FXML
    YearMonthView calendar = new YearMonthView();

    @FXML
    ImageView avatarr = new ImageView(image);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {



        AnchorPane root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));

        avatarr.setFitHeight(45);
        avatarr.setFitWidth(45);
        avatarr.setY(615);
        avatarr.setX(10);
        Circle circle = new Circle(32,637,20);
        Circle imageBorder = new Circle(32,637,22);
        imageBorder.setFill(Color.color(0.90,0.90,0.90));
        avatarr.setClip(circle);

        root.getChildren().addAll(imageBorder,avatarr);


        //root.getStylesheets().add("AdminDashboard.css");
        Scene scene = new Scene(root,1200,704);



        scene.getStylesheets().add("AdminDashboard.css");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public void setCalendarAppearance(){



    }

}

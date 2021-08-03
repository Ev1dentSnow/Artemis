module Artemis{

    requires javafx.fxml;
    requires com.google.gson;
    requires com.auth0.jwt;
    requires org.apache.httpcomponents.httpcore;
    requires json;
    requires com.calendarfx.view;
    requires org.apache.httpcomponents.httpclient;
    requires com.jfoenix;

    opens Artemis.Controllers to javafx.graphics, javafx.fxml;
    opens Artemis.Models to javafx.base, com.google.gson;
    opens Artemis.Models.Weather to com.google.gson;


    exports Artemis;
}
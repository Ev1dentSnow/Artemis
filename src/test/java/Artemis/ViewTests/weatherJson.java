package Artemis.ViewTests;

import Artemis.Models.Weather.ForecastWeather;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class weatherJson {




    public static void main(String[] args) throws IOException {

        String json = Files.readString(Path.of("/Users/moagimoja/Documents/weather_response.json"));
        Gson gson = new Gson();
        ForecastWeather forecastWeather = gson.fromJson(json, ForecastWeather.class);
    }

}

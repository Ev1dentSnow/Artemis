package Artemis.ViewTests;

import Artemis.App;
import Artemis.Models.Weather;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class weatherJson {




    public static void main(String[] args) throws IOException {

        String json = Files.readString(Path.of("/Users/moagimoja/Documents/weather_response.json"));
        Gson gson = new Gson();
        Weather weather = gson.fromJson(json, Weather.class);
    }

}

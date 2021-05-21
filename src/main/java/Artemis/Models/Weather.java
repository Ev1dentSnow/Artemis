package Artemis.Models;

import Artemis.App;
import javafx.scene.image.Image;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Weather {

    private JSONObject currentWeatherData;
    private JSONObject forecastWeatherData;
    private Image weatherImage;

    public Weather(JSONObject[] weatherData) throws JSONException {
        this.currentWeatherData = weatherData[1];
        this.forecastWeatherData = weatherData[2];

       JSONArray weatherSection = currentWeatherData.getJSONArray("weather");
       String weatherType = currentWeatherData.getString("main");
       String weatherDescription = currentWeatherData.getString("description");

       /*   For list of weather conditions provided by the openweathermaps api to understand
            this part better, please visit:
            https://openweathermap.org/weather-conditions
       */

       if(weatherType == "Thunderstorm"){
            weatherImage = new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-storm-96.png"));
       }
       else if(weatherType == "Drizzle"){
           weatherImage = new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-rain-96.png"));
       }
       else if(weatherType == "Rain"){
           weatherImage = new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-rain-96.png"));
       }
       else if(weatherType == "Snow"){ //haha, not in South Africa
           weatherImage = new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-rain-96.png"));
       }
       else if(weatherType == "Atmosphere"){

       }
       else if(weatherType == "Clear"){

       }
       else if(weatherType == "Clouds"){
           weatherImage = determineCloudsType(weatherDescription);
       }

    }

    public JSONObject getCurrentWeatherData() {
        return currentWeatherData;
    }

    public void setCurrentWeatherData(JSONObject currentWeatherData) {
        this.currentWeatherData = currentWeatherData;
    }

    public JSONObject getForecastWeatherData() {
        return forecastWeatherData;
    }

    public void setForecastWeatherData(JSONObject forecastWeatherData) {
        this.forecastWeatherData = forecastWeatherData;
    }

   /*This method is necessary because there are many types of cloudy weather such as
     partly cloudy, overcast, etc.
   */
    private Image determineCloudsType(String description){
        if(description == "overcast clouds"){
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-clouds-96.png"));
        }
        else if(description == "broken clouds"){
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-clouds-96.png"));
        }
        else if(description == "scattered clouds"){
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-partly-cloudy-day-96.png"));
        }
        else if(description == "few clouds"){
            return new Image(App.class.getResourceAsStream("/fxmlAssets/Weather/icons8-partly-cloudy-day-96.png"));
        }
        return null;
    }

    private Image prepareSixDayWeather(JSONObject forecastWeatherData) throws JSONException {
        Calendar calendar = Calendar.getInstance();
        int[] days = new int[5]; //There is current weather, then 6 more days, therefore total = 7
        HashMap<Integer, JSONObject> dailyForecast = new HashMap<>();
        JSONArray dailyWeather = forecastWeatherData.getJSONArray("daily");
        for(int i = 0; i < 6; i++){
            JSONObject day = (JSONObject) dailyWeather.get(i);
            JSONObject temp = day.getJSONObject("temp");
            double minimumTemp = temp.getDouble("min");
            double maximumTemp = temp.getDouble("max");
            JSONArray weather = dailyWeather.getJSONArray(0);
            JSONObject weatherStuff = (JSONObject) weather.get(0);
            String weatherGroup = weatherStuff.getString("main");
            String weatherDescription = weatherStuff.getString("description");
            dailyForecast.put(calendar.get(Calendar.DAY_OF_WEEK), (JSONObject) dailyWeather.get(i));
            calendar.add(Calendar.DAY_OF_YEAR, 1); //move calendar one day forward
        }


        return null;
    }

}

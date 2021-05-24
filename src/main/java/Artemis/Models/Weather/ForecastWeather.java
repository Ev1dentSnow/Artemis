package Artemis.Models.Weather;

public class ForecastWeather {

    private String latitude;
    private String longitude;
    private String timeZone;
    private Daily[] daily;

    public ForecastWeather(String latitude, String longitude, String timeZone, Daily[] daily) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
        this.daily = daily;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Daily[] getDaily() {
        return daily;
    }

    public void setDaily(Daily[] daily) {
        this.daily = daily;
    }
}

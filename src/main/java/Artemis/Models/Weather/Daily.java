package Artemis.Models.Weather;

public class Daily {

    //Note: If at any point camelCase is not used, it is due to the Gson library requirements
    private long dt; //Unix timestamp
    private Temp temp;
    private Weather[] weather;
    private double wind_speed;
    private double wind_deg;

    public Daily(long dt, Temp temp, Weather[] weather, double wind_speed, double wind_deg){
        this.dt = dt;
        this.temp = temp;
        this.weather = weather;
        this.wind_speed = wind_speed;
        this.wind_deg = wind_deg;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(double wind_speed) {
        this.wind_speed = wind_speed;
    }

    public double getWind_deg() {
        return wind_deg;
    }

    public void setWind_deg(double wind_deg) {
        this.wind_deg = wind_deg;
    }
}

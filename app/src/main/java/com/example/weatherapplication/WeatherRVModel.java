package com.example.weatherapplication;

public class WeatherRVModel {
    private String time;
    private String temperature;
    private String item;
    private String windSpeed;

    public WeatherRVModel(String time, String temperature, String item, String windSpeed) {
        this.time = time;
        this.temperature = temperature;
        this.item = item;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}

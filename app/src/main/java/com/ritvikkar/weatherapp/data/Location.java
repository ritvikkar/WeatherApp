package com.ritvikkar.weatherapp.data;

public class Location {

    private String city;
    private double temp;
    private String description;
    private String icon;

    public Location(String city, double temp, String description, String icon) {
        this.city = city;
        this.temp = temp;
        this.description = description;
        this.icon = icon;
    }

    public String getCity() {
        return city;
    }

    public double getTemp() {
        return temp;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}

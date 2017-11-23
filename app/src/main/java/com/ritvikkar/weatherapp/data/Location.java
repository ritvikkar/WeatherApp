package com.ritvikkar.weatherapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Location extends RealmObject {

    @PrimaryKey
    private String id;

    private String city;
    private double temp;
    private String description;
    private String icon;

    public Location() {}

    public void setValues(String city, double temp, String description, String icon) {
        this.city = city;
        this.temp = temp;
        this.description = description;
        this.icon = icon;
    }

    public String getId() {
        return id;
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

package com.example.geofencing_1;

public class LocationHelperDB {

    String lat, lon;

    public LocationHelperDB() {
    }

    public LocationHelperDB(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}

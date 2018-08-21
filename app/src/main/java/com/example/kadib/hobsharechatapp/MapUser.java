package com.example.kadib.hobsharechatapp;

/**
 * Created by kadibibas on 20.8.2018.
 */

public class MapUser {

    public String Device_token;
    public String Visibility;
    public String Lat;
    public String Lng;

    public MapUser(){

    }

    public MapUser(String name, String image, String status, String thumb_image,String Device_token,String Visibility,String Lat,String Lng) {
        this.Device_token = Device_token;
        this.Visibility = Visibility;
        this.Lat = Lat;
        this.Lng = Lng;
    }

    public String getDevice_token() {
        return Device_token;
    }

    public void setDevice_token(String device_token) {
        Device_token = device_token;
    }

    public String getVisibility() {
        return Visibility;
    }

    public void setVisibility(String visibility) {
        Visibility = visibility;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {Lng = lng;}

}

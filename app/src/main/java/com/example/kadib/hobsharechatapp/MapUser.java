package com.example.kadib.hobsharechatapp;

/**
 * Created by kadibibas on 20.8.2018.
 */

public class MapUser {

    public String Device_token;
    public String Visibility;
    public String Lat;
    public String Lng;
    public String firebase_id;
    public String Name;
    public String Status;

    public MapUser(){

    }

    public MapUser(String Device_token,String Visibility,String Lat,String Lng, String mfirebase_id, String Name, String Status) {
        this.Device_token = Device_token;
        this.Visibility = Visibility;
        this.Lat = Lat;
        this.Lng = Lng;
        this.firebase_id = mfirebase_id;
        this.Name = Name;
        this.Status = Status;
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

    public String getfirebase_id() {
        return firebase_id;
    }

    public void setfirebase_id(String mfirebase_id) {firebase_id = mfirebase_id;}

    public String getName() {
        return Name;
    }

    public void setName(String name) {Name = name;}

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {Status = status;}

}

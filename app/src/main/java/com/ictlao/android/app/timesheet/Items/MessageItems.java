package com.ictlao.android.app.timesheet.Items;

public class MessageItems {
    private String name = "";
    private boolean isRead = false;
    private String userId = "";
    private String time = "";
    private String date = "";
    private String profile_url = "";
    private double latitude = 0;
    private double longitude = 0;
    public MessageItems(){}
    public MessageItems(String name,boolean isRead,String userId,String time,String date, String profile_url, double latitude,double longitude){
        this.name = name;
        this.isRead = isRead;
        this.userId = userId;
        this.time = time;
        this.date = date;
        this.profile_url = profile_url;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

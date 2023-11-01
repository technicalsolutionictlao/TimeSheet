package com.ictlao.android.app.timesheet.Items;

public class TimeSheetItems {
    private String timeSheetId = "";
    private String userId = "";
    private String date = "";
    private String checkStatus = "";
    private String timeInMarker = "";
    private String checkInTime = "";
    private double latitude = 0;
    private double longitude = 0;
    private String description = "";
    private String checkOutTime = "";
    private String timeOutMarker = "";
    public TimeSheetItems(){}
    public TimeSheetItems(String timeSheetId, String userId, String date,
                          String checkStatus, String timeInMarker, String checkInTime,
                          double latitude, double longitude, String description,
                          String checkOutTime,String timeOutMarker){
        this.timeSheetId = timeSheetId;
        this.userId = userId;
        this.date = date;
        this.checkStatus = checkStatus;
        this.timeInMarker = timeInMarker;
        this.checkInTime = checkInTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.checkOutTime = checkOutTime;
        this.timeOutMarker = timeOutMarker;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public String getTimeOutMarker() {
        return timeOutMarker;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setTimeOutMarker(String timeOutMarker) {
        this.timeOutMarker = timeOutMarker;
    }

    public String getDescription() {
        return description;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public String getTimeInMarker() {
        return timeInMarker;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setTimeInMarker(String timeInMarker) {
        this.timeInMarker = timeInMarker;
    }

    public String getTimeSheetId() {
        return timeSheetId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public String getDate() {
        return date;
    }

    public void setTimeSheetId(String timeSheetId) {
        this.timeSheetId = timeSheetId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

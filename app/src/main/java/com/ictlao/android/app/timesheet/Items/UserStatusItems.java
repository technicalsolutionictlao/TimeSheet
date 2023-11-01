package com.ictlao.android.app.timesheet.Items;

public class UserStatusItems {
    private String statusId = "";
    private String userId = "";
    private String status = "";
    private String time = "";
    private String date = "";
    public UserStatusItems(){}
    public UserStatusItems(String statusId,String userId,String status,String time,String date){
        this.statusId = statusId;
        this.userId = userId;
        this.status = status;
        this.time = time;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}

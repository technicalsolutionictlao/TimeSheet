package com.ictlao.android.app.timesheet.Items;

import android.provider.ContactsContract;

public class UserItems {
    private String name = "";
    private String userId = "";
    private String email = "";
    private String profile_url = "";
    private int sick_day = 0;
    private int vacation_day = 0;
    public UserItems(){}
    public UserItems(String name, String userId, String email,String profile_url,int sick_day,int vacation_day){
        this.name = name;
        this.userId = userId;
        this.email = email;
        this.profile_url = profile_url;
        this.sick_day = sick_day;
        this.vacation_day = vacation_day;
    }

    public int getSick_day() {
        return sick_day;
    }

    public int getVacation_day() {
        return vacation_day;
    }

    public void setSick_day(int sick_day) {
        this.sick_day = sick_day;
    }

    public void setVacation_day(int vacation_day) {
        this.vacation_day = vacation_day;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }
}

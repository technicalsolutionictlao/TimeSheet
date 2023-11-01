package com.ictlao.android.app.timesheet.Items;

public class HollyDayItems {
    private String hollyDayId = "";
    private int vacationDay = 0;
    private int sickDay = 0;
    private String userId = "";
    private String name = "";
    private String profile_url = "";
    private String email = "";
    public HollyDayItems(){}
    public HollyDayItems(String hollyDayId,int vacationDay,int sickDay,String userId,String name,String profile_url,String email){
        this.hollyDayId = hollyDayId;
        this.vacationDay = vacationDay;
        this.sickDay = sickDay;
        this.userId = userId;
        this.name = name;
        this.profile_url = profile_url;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public String getUserId() {
        return userId;
    }

    public int getSickDay() {
        return sickDay;
    }

    public int getVacationDay() {
        return vacationDay;
    }

    public String getHollyDayId() {
        return hollyDayId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHollyDayId(String hollyDayId) {
        this.hollyDayId = hollyDayId;
    }

    public void setSickDay(int sickDay) {
        this.sickDay = sickDay;
    }

    public void setVacationDay(int vacationDay) {
        this.vacationDay = vacationDay;
    }
}

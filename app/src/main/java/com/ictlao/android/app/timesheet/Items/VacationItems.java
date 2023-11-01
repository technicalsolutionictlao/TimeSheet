package com.ictlao.android.app.timesheet.Items;

public class VacationItems {

    private String vacationId = "";
    private String date = "";
    private String time = "";
    private String useVacationCase = "";
    private String remark = "";
    private String userId = "";
    private String profile_url = "";
    private String name = "";
    private String email = "";
    private String day_qty = "";
    private String start_date = "";
    private String end_date = "";

    public VacationItems(){}
    public VacationItems(String vacationId,String date,String time,String useVacationCase,
                         String remark,String userId,String profile_url,String name,
                         String email,String day_qty,String start_date,String end_date){
        this.vacationId = vacationId;
        this.date = date;
        this.time = time;
        this.useVacationCase = useVacationCase;
        this.remark = remark;
        this.userId = userId;
        this.profile_url = profile_url;
        this.name = name;
        this.email = email;
        this.day_qty = day_qty;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDay_qty() {
        return day_qty;
    }

    public void setDay_qty(String day_qty) {
        this.day_qty = day_qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getRemark() {
        return remark;
    }

    public String getUseVacationCase() {
        return useVacationCase;
    }

    public String getVacationId() {
        return vacationId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setUseVacationCase(String useVacationCase) {
        this.useVacationCase = useVacationCase;
    }

    public void setVacationId(String vacationId) {
        this.vacationId = vacationId;
    }
}

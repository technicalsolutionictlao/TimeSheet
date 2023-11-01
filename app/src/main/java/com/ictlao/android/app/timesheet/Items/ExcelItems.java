package com.ictlao.android.app.timesheet.Items;

public class ExcelItems {
    private String id = "";
    private String name = "";
    private String date = "";
    private String checkInTime = "";
    private String check_status = "";
    private String time_in_marker = "";
    private String description = "";
    private String checkOutTime = "";
    private String time_out_marker = "";

    public ExcelItems(){}

    public ExcelItems(String id, String name, String date, String checkInTime,
                      String check_status, String time_in_marker, String description,
                      String checkOutTime,String time_out_marker){
        this.id = id;
        this.name = name;
        this.date = date;
        this.checkInTime = checkInTime;
        this.check_status = check_status;
        this.time_in_marker = time_in_marker;
        this.description = description;
        this.checkOutTime = checkOutTime;
        this.time_out_marker = time_out_marker;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public String getTime_out_marker() {
        return time_out_marker;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setTime_out_marker(String time_out_marker) {
        this.time_out_marker = time_out_marker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public String getDate() {
        return date;
    }

    public String getCheck_status() {
        return check_status;
    }

    public String getId() {
        return id;
    }

    public String getTime_in_marker() {
        return time_in_marker;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime_in_marker(String time_in_marker) {
        this.time_in_marker = time_in_marker;
    }
}

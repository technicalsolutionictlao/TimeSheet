package com.ictlao.android.app.timesheet.Items;

public class WorkTimeItems {

    private String part = "";
    private String timeIn = "";
    private String timeOut = "";

    public WorkTimeItems() {}

    public WorkTimeItems(String part, String timeIn, String timeOut) {
        this.part = part;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getPart() {
        return part;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }
}

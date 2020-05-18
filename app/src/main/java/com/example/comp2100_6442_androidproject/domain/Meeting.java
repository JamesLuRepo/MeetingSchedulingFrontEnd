package com.example.comp2100_6442_androidproject.domain;

public class Meeting {
    private Integer mid;
    private String name;
    private String notes;
    private String holdTime;
    private Integer timeLength;
    private String location;
    private Integer gpsGid;

    public Meeting(){
    }

    public Meeting(Integer mid, String name, String notes, String holdTime, Integer timeLength, String location, Integer gpsGid) {
        this.mid = mid;
        this.name = name;
        this.notes = notes;
        this.holdTime = holdTime;
        this.timeLength = timeLength;
        this.location = location;
        this.gpsGid = gpsGid;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(String holdTime) {
        this.holdTime = holdTime;
    }

    public Integer getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(Integer timeLength) {
        this.timeLength = timeLength;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getGpsGid() {
        return gpsGid;
    }

    public void setGpsGid(Integer gpsGid) {
        this.gpsGid = gpsGid;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "mid=" + mid +
                ", name='" + name + '\'' +
                ", notes='" + notes + '\'' +
                ", holdTime='" + holdTime + '\'' +
                ", timeLength=" + timeLength +
                ", location='" + location + '\'' +
                ", gpsGid=" + gpsGid +
                '}';
    }
}

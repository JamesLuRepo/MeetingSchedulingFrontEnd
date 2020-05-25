package com.example.comp2100_6442_androidproject.domain;

public class Preference {

    private Integer pid;
    private String startTime;
    private String endTime;
    private String userEmail;

    public Preference() {
    }

    public Preference(Integer pid, String startTime, String endTime, String userEmail) {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userEmail = userEmail;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "Preference{" +
                "pid=" + pid +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}

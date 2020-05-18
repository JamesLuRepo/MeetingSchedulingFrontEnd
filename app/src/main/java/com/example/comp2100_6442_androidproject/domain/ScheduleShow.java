package com.example.comp2100_6442_androidproject.domain;

public class ScheduleShow {

    private Integer mid;
    private String name;
    private String info;

    public ScheduleShow() {
    }

    public ScheduleShow(Integer mid, String name, String info) {
        this.mid = mid;
        this.name = name;
        this.info = info;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "ScheduleShow{" +
                "mid=" + mid +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}

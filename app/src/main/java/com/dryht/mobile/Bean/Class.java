package com.dryht.mobile.Bean;

public class Class {
    private int classid;
    private String name;
    private String intro;
    private int status;
    private int courseid;
    private String place;
    private String time;
    private int count;
    private int teacherid;
    private String weekday;
    private String total;
    private String teachname;

    public Class(String classid, String place, String name, String tname, String time) {
        this.classid = Integer.parseInt(classid);
        this.place = place;
        this.name = name;
        this.teachname = tname;
        this.time = time;
    }

    public Class(String classid, String place, String name, String time) {
        this.classid = Integer.parseInt(classid);
        this.place = place;
        this.name = name;
        this.time = time;
    }

    public String getTeachname() {
        return teachname;
    }

    public void setTeachname(String teachname) {
        this.teachname = teachname;
    }

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(int teacherid) {
        this.teacherid = teacherid;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

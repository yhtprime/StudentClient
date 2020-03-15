package com.dryht.mobile.Bean;

public class Lesson extends cn.devmeteor.tableview.Lesson {

    private int classid;
    private String week;
    private String name;
    private String weekday;
    private int start;
    private int end;
    private String place;
    private String teacher;

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public String getName() {
        return name;
    }

    public String getWeekday() {
        return weekday;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getPlace() {
        return place;
    }
    public String getTeacher() {
        return teacher;
    }


    public Lesson(String classid, String week, String name, String weekday, int start, int end, String place,String teacher) {
        super(classid,week,name,weekday,start,end,place);
        this.classid = Integer.parseInt(classid);
        this.week = week;
        this.name = name;
        this.weekday = weekday;
        this.start = start;
        this.end = end;
        this.place = place;
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return name+place+teacher;
    }
}
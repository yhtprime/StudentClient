package com.dryht.mobile.Bean;

import java.util.List;

public class CheckHistory {
    private int checkhistoryid;
    private int good;
    private int bad;
    private List<Student> studentList;

    public CheckHistory(int good, int bad, List<Student> studentList) {
        this.good = good;
        this.bad = bad;
        this.studentList = studentList;
    }

    public int getCheckhistoryid() {
        return checkhistoryid;
    }

    public void setCheckhistoryid(int checkhistoryid) {
        this.checkhistoryid = checkhistoryid;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

}

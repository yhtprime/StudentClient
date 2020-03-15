package com.dryht.mobile.Bean;

import java.sql.Time;
import java.sql.Timestamp;

public class Circle {

    /**
     * circleid : 11
     * name : 刘洋
     * followid : 11
     * title : 第一个说说
     * intro : 1
     * time : 2020-02-28 14:02:18
     * pic : http://cd.dryht.cn:25200/static/pic/59b2434e0c5d4117b2f3a71a83b92b4e.png
     * image : http://cd.dryht.cn:25200/static/pengyou/01e378d0fb204bd9bb0d3ddc33d53bd2.png
     * zan : 4
     * com : 0
     * read : 8
     * islike : 0
     */

    private int circleid;
    private String name;
    private String intro;
    private Timestamp time;
    private String pic1;
    private String pic2;
    private String pic3;
    private int status;
    private int zan;
    private int com;
    private int read;
    private Student student;
    private Teacher teacher;

    public Circle(String name, String intro, Timestamp time, String pic1, String pic2, String pic3, int status) {
        this.circleid = circleid;
        this.name = name;
        this.intro = intro;
        this.time = time;
        this.pic1 = pic1;
        this.pic2 = pic2;
        this.pic3 = pic3;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getCircleid() {
        return circleid;
    }

    public void setCircleid(int circleid) {
        this.circleid = circleid;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    public int getCom() {
        return com;
    }

    public void setCom(int com) {
        this.com = com;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

}

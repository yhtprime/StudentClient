package com.dryht.mobile.Bean;

import java.sql.Timestamp;

public class Check {

    /**
     * checkid : 23
     * time : 2020-03-13T01:15:34Z
     * name : 测试课程1/3/5
     * teacherid : 1
     * classid : 16
     * status : 1
     */

    private int checkid;
    private Timestamp time;
    private String name;
    private int teacherid;
    private int classid;
    private int status;

    public Check(int checkid,Timestamp time,String name,int teacherid,int classid,int status){
        this.checkid = checkid;
        this.time = time;
        this.name = name;
        this.teacherid = teacherid;
        this.classid =classid;
        this.status = status;
    }

    public int getCheckid() {
        return checkid;
    }

    public void setCheckid(int checkid) {
        this.checkid = checkid;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(int teacherid) {
        this.teacherid = teacherid;
    }

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

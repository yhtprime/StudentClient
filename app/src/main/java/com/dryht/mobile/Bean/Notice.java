package com.dryht.mobile.Bean;

import java.sql.Timestamp;

public class Notice {
    private int infoid;
    private String intro;
    private String name;
    private int teacherid;
    private int classid;
    private Timestamp date;
    private int status;
    private String cname;
    private String tname;

    public Notice(int infoid, String cname, String date, String tname, String name, String intro) {
        this.infoid = infoid;
        this.cname = cname;
        this.tname = tname;
        this.name = name;
        this.intro = intro;
        this.date = Timestamp.valueOf(date);
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public int getInfoid() {
        return infoid;
    }

    public void setInfoid(int infoid) {
        this.infoid = infoid;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

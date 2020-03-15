package com.dryht.mobile.Bean;

public class Teacher {
    private int teacherid;
    private String account;
    private String passwd;
    private String name;
    private String pic;
    private String facedata;
    private int majorid;
    private String email;
    private String intro;
    private String background;
    private int status;


    public Teacher(String name, String pic) {
        this.name = name;
        this.pic = pic;
    }

    public Teacher(int studentid, String account, String passwd, String name, String headpic, String facedata, int majorid, String email, String intro, String background, int status) {
        this.teacherid = studentid;
        this.account = account;
        this.name = name;
        this.pic = headpic;
        this.facedata = facedata;
        this.majorid = majorid;
        this.email = email;
        this.intro = intro;
        this.background = background;
        this.status = status;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(int teacherid) {
        this.teacherid = teacherid;
    }

    public String getAccount() {
        return account;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacedata() {
        return facedata;
    }

    public void setFacedata(String facedata) {
        this.facedata = facedata;
    }


    public int getMajorid() {
        return majorid;
    }

    public void setMajorid(int majorid) {
        this.majorid = majorid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

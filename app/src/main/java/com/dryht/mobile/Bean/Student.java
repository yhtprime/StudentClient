package com.dryht.mobile.Bean;

public class Student {
    private int studentid;
    private String account;
    private String passwd;
    private String name;
    private String headpic;
    private String facedata;
    private int grade;
    private int majorid;
    private String email;
    private String intro;
    private String background;
    private int status;


    public Student(String name, String headpic) {
        this.name = name;
        this.headpic = headpic;
    }

    public Student(int studentid, String account, String passwd, String name, String headpic, String facedata, int grade, int majorid, String email, String intro, String background, int status) {
        this.studentid = studentid;
        this.account = account;
        this.name = name;
        this.headpic = headpic;
        this.facedata = facedata;
        this.grade = grade;
        this.majorid = majorid;
        this.email = email;
        this.intro = intro;
        this.background = background;
        this.status = status;
    }

    public int getStudentid() {
        return studentid;
    }

    public void setStudentid(int studentid) {
        this.studentid = studentid;
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


    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getFacedata() {
        return facedata;
    }

    public void setFacedata(String facedata) {
        this.facedata = facedata;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
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

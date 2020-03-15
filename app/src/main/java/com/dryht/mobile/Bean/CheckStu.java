package com.dryht.mobile.Bean;

import java.sql.Timestamp;

public class CheckStu {
    private int checkstuId;
    private int status;
    private int checkId;
    private int studentId;
    private Timestamp time;

    public CheckStu(int checkstuId, int status, int checkId, int studentId, Timestamp time) {
        this.checkstuId = checkstuId;
        this.status = status;
        this.checkId = checkId;
        this.studentId = studentId;
        this.time = time;
    }

    public int getCheckstuId() {
        return checkstuId;
    }

    public void setCheckstuId(int checkstuId) {
        this.checkstuId = checkstuId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}

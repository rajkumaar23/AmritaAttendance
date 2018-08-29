package com.krca.attendance;

public class Student {

    private String mstudentId;
    private boolean mpresence;
    public Student(String studentId,boolean presence) {
        mstudentId=studentId;
        mpresence=presence;

    }
    public String getMstudentId() {
        return mstudentId;
    }
    public boolean getmPresence() {
        return mpresence;
    }
}

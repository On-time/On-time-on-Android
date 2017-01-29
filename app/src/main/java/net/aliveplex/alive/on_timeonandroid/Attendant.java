package net.aliveplex.alive.on_timeonandroid;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by snail on 1/27/2017.
 */

public class Attendant extends RealmObject{
    @PrimaryKey
    private String subjectID;

    private String subjectSection;
    private String studentID;
    private String attendanceTime;

    public String getsubjectID() {return subjectID;}
    public void setsubjectID(String subjectID) {this.subjectID = subjectID;}
    public String getsubjectSection() {return subjectSection;}
    public void setsubjectSection(String subjectSection) {this.subjectSection = subjectSection;}
    public String getstudentID() {return studentID;}
    public void setstudentID(String studentID) {this.studentID = studentID;}
    public String getattendanceTime() {return attendanceTime;}
    public void setattendanceTime(String attendanceTime) {this.attendanceTime = attendanceTime;}
}

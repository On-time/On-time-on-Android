package net.aliveplex.alive.on_timeonandroid;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by snail on 1/27/2017.
 */

public class SubjectStudent extends RealmObject{
    @PrimaryKey
    private String ID; // รหัสนักศึกษา-รหัสวิชา-sec

    public String getID() {return ID;}
    public void setID(String ID) {this.ID = ID;}
}

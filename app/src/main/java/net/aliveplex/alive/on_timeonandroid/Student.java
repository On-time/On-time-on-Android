package net.aliveplex.alive.on_timeonandroid;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by snail on 1/27/2017.
 */

public class Student extends RealmObject {

    @PrimaryKey
    private String ID;

    private String AndroidID;

    public String getID() {return ID;}
    public void setID(String ID) {this.ID = "";}
    public String getAndroidID() {return AndroidID;}
    public void setAndroidID(String ID) {this.AndroidID = "";}

}



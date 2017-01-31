package net.aliveplex.alive.on_timeonandroid;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by snail on 1/27/2017.
 */

public class Subject extends RealmObject{

    @PrimaryKey
    private String ID; // เอารหัสวิชา ต่อกับ sec ต่อกับ INT-1012


    public String getID() {return ID;}
    public void setID(String ID) {this.ID = ID;}
}

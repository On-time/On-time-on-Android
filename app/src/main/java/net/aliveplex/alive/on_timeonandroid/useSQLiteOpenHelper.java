package net.aliveplex.alive.on_timeonandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by snail on 1/24/2017.
 */

public class useSQLiteOpenHelper extends SQLiteOpenHelper{
    public useSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE SUBJECT(ID INTEGER PRIMARY KEY,SUBJECTNAME TEXT,SEC INTEGER)";
        db.execSQL(SQL);
        SQL = "CREATE TABLE STUDENT(ID INTEGER PRIMARY KEY,ANDROID_ID INTEGER)";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

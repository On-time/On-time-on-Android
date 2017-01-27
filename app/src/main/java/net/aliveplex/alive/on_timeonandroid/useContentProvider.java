package net.aliveplex.alive.on_timeonandroid;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by snail on 1/24/2017.
 */

public class useContentProvider extends ContentProvider{
    useSQLiteOpenHelper myDB;
    SQLiteDatabase db;

    static UriMatcher matcher;
    {
        matcher = new UriMatcher((UriMatcher.NO_MATCH));
        matcher.addURI("mydatabase","STUDENT",1);
        matcher.addURI("mydatabase","SUBJECT",2);
    }
    @Override
    public boolean onCreate() {
        myDB = new useSQLiteOpenHelper(getContext(),"teamDatabase.db",null,1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionAregs, String sortOrder) {
        Cursor c = null;
        int i;
        db = myDB.getReadableDatabase();
        i = matcher.match(uri);
        c = db.query("SUBJECT",projection,selection,selectionAregs,null,null,sortOrder);

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri u = null;
        long id=0;
        int i;
        i = matcher.match(uri);
        db = myDB.getWritableDatabase();
        id = db.insert("SUBJECT",null,values);
        u = ContentUris.withAppendedId(uri,id);

        return u;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

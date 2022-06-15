package com.matsavx.vkrtest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.PublicKey;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bumpDB";
    public static final String TABLE_BUMPS = "bumps";

    public static final String KEY_ID = "_id";
    public static final String KEY_ACCELEROMETER = "accelerometer";
    public static final String KEY_GYROSCOPE = "gyroscope";
    public static final String KEY_BUMP_TYPE = "bump_type";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_BUMPS + "(" + KEY_ID
                + " integer primary key," + KEY_ACCELEROMETER + " real,"
                + KEY_GYROSCOPE + " real," + KEY_BUMP_TYPE + " text,"
                + KEY_LATITUDE + " real," + KEY_LONGITUDE + " real" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_BUMPS);

        onCreate(db);
    }
}

package com.matsavx.vkrtest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DbManager(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void openDb() {
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDb(float accValue, float gyrValue, float latitude, float longitude, int velocity) {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.KEY_ACCELEROMETER, accValue);
        cv.put(DbConstants.KEY_GYROSCOPE, gyrValue);
        cv.put(DbConstants.KEY_LATITUDE, latitude);
        cv.put(DbConstants.KEY_LONGITUDE, longitude);
        cv.put(DbConstants.KEY_VELOCITY, velocity);

        db.insert(DbConstants.TABLE_NAME, null, cv);
    }

    public List<Float> getFromDb() {
        List<Float> tempList = new ArrayList<>();
        Cursor cursor = db.query(DbConstants.TABLE_NAME, null,null,null,null, null,null);
        while (cursor.moveToNext()) {
            float acc = cursor.getFloat(cursor.getColumnIndexOrThrow(DbConstants.KEY_ACCELEROMETER));
            tempList.add(acc);
        }
        cursor.close();
        return tempList;
    }

    public void closeDb() {
        dbHelper.close();
    }
}

package com.matsavx.vkrtest.database;

public class DbConstants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bumpDB";
    public static final String TABLE_NAME = "bumps";

    public static final String KEY_ID = "_id";
    public static final String KEY_ACCELEROMETER = "accelerometer";
    public static final String KEY_GYROSCOPE = "gyroscope";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_VELOCITY = "velocity";

    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + "(" + KEY_ID
            + " INTEGER PRIMARY KEY," + KEY_ACCELEROMETER + " REAL,"
            + KEY_GYROSCOPE + " REAL," + KEY_LATITUDE + " REAL,"
            + KEY_LONGITUDE + " REAL" + KEY_VELOCITY + " INTEGER)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

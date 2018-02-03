package com.es2.mapacrud.database.handlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotoDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "photos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PHOTOS = "photos";
    public static final String COLUMN_ID = "photoId";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_Z = "z";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_PHOTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PATH + " TEXT, " +
                    COLUMN_LNG + " REAL, " +
                    COLUMN_LAT + " REAL, " +
                    COLUMN_X + " REAL, " +
                    COLUMN_Y + " REAL, " +
                    COLUMN_Z + " REAL " +
                    ")";

    public PhotoDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        db.execSQL(TABLE_CREATE);
    }
}

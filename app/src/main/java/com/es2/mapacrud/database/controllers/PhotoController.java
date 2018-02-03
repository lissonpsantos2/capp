package com.es2.mapacrud.database.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.es2.mapacrud.database.handlers.PhotoDBHandler;
import com.es2.mapacrud.database.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoController {
    public static final String LOG_TAG_OK = "PHOTO_DB_SYS";
    public static final String LOG_TAG_ERR = "PHOTO_DB_SYS_ERROR";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            PhotoDBHandler.COLUMN_ID,
            PhotoDBHandler.COLUMN_PATH,
            PhotoDBHandler.COLUMN_LNG,
            PhotoDBHandler.COLUMN_LAT,
            PhotoDBHandler.COLUMN_X,
            PhotoDBHandler.COLUMN_Y,
            PhotoDBHandler.COLUMN_Z
    };

    public PhotoController(Context context) {
        this.dbHandler = new PhotoDBHandler(context);
    }

    public void open() {
        try {
            database = dbHandler.getWritableDatabase();
            Log.i(LOG_TAG_OK, "Banco de dados aberto!");
        }catch (SQLiteException e) {
            Log.i(LOG_TAG_ERR, e.getMessage());
        }
    }

    public void close() {
        dbHandler.close();
        Log.i(LOG_TAG_OK, "Banco de dados fechado!");
    }

    public Photo addPhoto (Photo photo) {
        ContentValues data  = new ContentValues();
        data.put(PhotoDBHandler.COLUMN_PATH, photo.getPath());
        data.put(PhotoDBHandler.COLUMN_LNG, photo.getLng());
        data.put(PhotoDBHandler.COLUMN_LAT, photo.getLat());
        data.put(PhotoDBHandler.COLUMN_X, photo.getX());
        data.put(PhotoDBHandler.COLUMN_Y, photo.getY());
        data.put(PhotoDBHandler.COLUMN_Z, photo.getZ());
        photo.setPhotoId(database.insert(PhotoDBHandler.TABLE_PHOTOS,null, data));
        return photo;
    }

    public Photo getPhoto(long id) {
        Cursor cursor = database.query(PhotoDBHandler.TABLE_PHOTOS, allColumns,PhotoDBHandler.COLUMN_ID + "=?", new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Photo selectedPhoto = new Photo(Long.parseLong(cursor.getString(0)), cursor.getString(1),cursor.getFloat(2),cursor.getFloat(3),cursor.getFloat(4),cursor.getFloat(5),cursor.getFloat(6));
        return selectedPhoto;
    }

    public List<Photo> getAllPhotos() {
        Cursor cursor = database.query(PhotoDBHandler.TABLE_PHOTOS, allColumns,null,null,null, null, null);
        List<Photo> photos = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Photo photo = new Photo();
                photo.setPhotoId(cursor.getLong(cursor.getColumnIndex(PhotoDBHandler.COLUMN_ID)));
                photo.setPath(cursor.getString(cursor.getColumnIndex(PhotoDBHandler.COLUMN_PATH)));
                photo.setLng(cursor.getFloat(cursor.getColumnIndex(PhotoDBHandler.COLUMN_LNG)));
                photo.setLat(cursor.getFloat(cursor.getColumnIndex(PhotoDBHandler.COLUMN_LAT)));
                photo.setX(cursor.getFloat(cursor.getColumnIndex(PhotoDBHandler.COLUMN_X)));
                photo.setY(cursor.getFloat(cursor.getColumnIndex(PhotoDBHandler.COLUMN_Y)));
                photo.setZ(cursor.getFloat(cursor.getColumnIndex(PhotoDBHandler.COLUMN_Z)));
                photos.add(photo);
            }
        }
        return photos;
    }

    public int updatePhoto(Photo photo) {
        ContentValues data = new ContentValues();
        data.put(PhotoDBHandler.COLUMN_PATH, photo.getPath());
        data.put(PhotoDBHandler.COLUMN_LNG, photo.getLng());
        data.put(PhotoDBHandler.COLUMN_LAT, photo.getLat());
        data.put(PhotoDBHandler.COLUMN_X, photo.getX());
        data.put(PhotoDBHandler.COLUMN_Y, photo.getY());
        data.put(PhotoDBHandler.COLUMN_Z, photo.getZ());
        return database.update(PhotoDBHandler.TABLE_PHOTOS, data,PhotoDBHandler.COLUMN_ID + "=?",new String[] { String.valueOf(photo.getPhotoId())});
    }

    public void removePhoto(Photo photo) {
        database.delete(PhotoDBHandler.TABLE_PHOTOS, PhotoDBHandler.COLUMN_ID + "=" + photo.getPhotoId(), null);
    }

}

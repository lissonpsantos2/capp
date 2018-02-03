package com.es2.mapacrud.database.models;

public class Photo {
    private long photoId;
    private String path;
    private float lng;
    private float lat;
    private float x;
    private float y;
    private float z;

    public Photo(long photoId, String path, float lng, float lat, float x, float y, float z) {
        this.photoId = photoId;
        this.path = path;
        this.lng = lng;
        this.lat = lat;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Photo() {

    }

    public long getPhotoId() {
        return photoId;
    }

    public String getPath() {
        return path;
    }

    public float getLng() {
        return lng;
    }

    public float getLat() {
        return lat;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photoId=" + photoId +
                ", path='" + path + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

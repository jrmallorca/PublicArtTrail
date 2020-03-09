package com.publicarttrail.googlemapspractice.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Base64;

// POJO converted from JSON
public class Artwork {
    // Base attributes
    private long id;
    private String name;
    private String creator;
    private String description;
    private double latitude;
    private double longitude;
    private String image;

    public Artwork(long id, String name, String creator, String description, double longitude, double latitude, String image ){
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // More complex attributes for methods
    private transient LatLng latLng;
    private transient Bitmap bitmap;

    public Artwork(long id, String name, String creator, String description, Double latitude,
                        Double longitude, String image){
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getLatLng() {
        if (latLng == null) latLng = new LatLng(latitude, longitude);
        return latLng;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            byte[] imgBytes = Base64.getDecoder().decode(image);
            bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        }
        return bitmap;
    }
}

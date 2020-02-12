package com.publicarttrail.googlemapspractice.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

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

    // More complex attributes for methods
    private LatLng latLng;
    private Bitmap bitmap;

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

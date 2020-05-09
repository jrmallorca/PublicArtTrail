package com.publicarttrail.googlemapspractice.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;


// POJO converted from JSON
public class Artwork {
    // Base attributes
    private int id;
    private String name;
    private String creator;
    private String description;
    private double latitude;
    private double longitude;
    private String image;

    // More complex attributes for methods
    private LatLng latLng;
    private Bitmap bitmap;

    public Artwork(int id,
                   String name,
                   String creator,
                   String description,
                   double latitude,
                   double longitude,
                   String image) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public int getId() {
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
            Log.d("en4", image);
           // byte[] imgBytes = Base64.getDecoder().decode(image);
            byte[] imgBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);

            bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

            //byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
            //return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
        return bitmap;
    }
}

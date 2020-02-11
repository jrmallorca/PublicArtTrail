package com.publicarttrail.googlemapspractice.pojo;

import com.google.android.gms.maps.model.LatLng;

// POJO converted from JSON
public class Artwork {
    // Base attributes
    private long id;
    private String name;
    private String creator;
    private String description;
    private double latitude;
    private double longitude;
    // private Image image; // TODO: Add later

    // More complex attributes for methods
    // TODO: 10/02/2020 Replace this with Image from Artwork when implemented
    private int drawableId;
    private LatLng latLng;

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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public LatLng getLatLng() {
        if (latLng == null) latLng = new LatLng(latitude, longitude);
        return latLng;
    }
}

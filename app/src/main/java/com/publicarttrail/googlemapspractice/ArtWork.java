package com.publicarttrail.googlemapspractice;

import android.graphics.Picture;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ArtWork {
    public String name;
    public ArrayList<Trail> trails;
    public String artistName;
    public String description;
    public LatLng latLng;
    public Picture picture;
    //corresponds to int of drawable png
    public Integer drawableId;


    ArtWork(String name, LatLng latLng){
        this.name = name;
        this.latLng = latLng;
    }

}

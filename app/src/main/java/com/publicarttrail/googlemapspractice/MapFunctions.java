package com.publicarttrail.googlemapspractice;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;



//the class acts as a superclass for alltrails(consists of all trails) and
// trail(consist of trail's artwork)

public class MapFunctions {

    public String name;
    public LatLng zoomInArea;
    public float zoomFactor;
    public GoogleMap map;

MapFunctions(GoogleMap map, String name){
    this.map = map;
    this.name = name;
}


public void zoomIn(){
   map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomInArea, zoomFactor));
}

}



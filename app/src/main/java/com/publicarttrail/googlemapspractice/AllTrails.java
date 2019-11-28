package com.publicarttrail.googlemapspractice;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AllTrails extends MapFunctions{

    //irrelevant as the hashmap is there
    public ArrayList<Trail> trails = new ArrayList<>();

    //hashmap to store trail and its marker
    public Map<Marker, Trail> hashmap = new HashMap<>();




    AllTrails(GoogleMap map, String name) {
        super(map, name);
        zoomFactor = 13;
    }


    public void addMarkers(){
        for (Trail trail:trails){
            Marker marker =  map.addMarker(new MarkerOptions().position(trail.zoomInArea).title(trail.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            hashmap.put(marker, trail);
        }
    }


}

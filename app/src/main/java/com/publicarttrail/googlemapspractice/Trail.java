package com.publicarttrail.googlemapspractice;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trail extends MapFunctions{

    //irrelevant
    public ArrayList<ArtWork> artWorks = new ArrayList<>();
    //hashmap to store trail and its marker
    public Map<Marker, ArtWork> hashmap = new HashMap<>();


    Trail(GoogleMap map, String name) {
            super(map, name);
            zoomFactor = 17;
        }

        public void addMarkers() {
            for (ArtWork artwork : artWorks) {
                Marker marker =  map.addMarker(new MarkerOptions().position(artwork.latLng).title(artwork.name));
                hashmap.put(marker, artwork);
            }
        }


        public void calculateMiddlePoint(Marker currentposition){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for(Map.Entry element:hashmap.entrySet()){
                Marker key = (Marker)element.getKey();
                builder.include(key.getPosition());
            }
            builder.include(currentposition.getPosition());
            LatLngBounds bounds = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //map.moveCamera(cu);
            map.animateCamera(cu);

        }



    }

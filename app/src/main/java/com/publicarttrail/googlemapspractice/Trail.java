package com.publicarttrail.googlemapspractice;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.publicarttrail.googlemapspractice.directionhelpers.FetchURL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trail {
    // Hashmap to store trail and its marker
    public Map<Marker, ArtWork> hashmap = new HashMap<>();
    public List<Marker> markers = new ArrayList<>();
    public String name;
    public LatLng zoomInArea;
    public float zoomFactor;
    public GoogleMap map;

    Trail(GoogleMap map, String name) {
        this.map = map;
        this.name = name;
        zoomFactor = 17;
    }

    // Adjusts visibility of artwork markers
    public void artworkMarkersVisibility(Boolean bool) {
        for (Map.Entry element : hashmap.entrySet()) {
            Marker key = (Marker) element.getKey();
            key.setVisible(bool);

        }
    }

    public void addMarker(ArtWork artwork) {

            Marker marker =  map.addMarker(new MarkerOptions().position(artwork.latLng).title(artwork.name).snippet(artwork.artistName));
            hashmap.put(marker, artwork);
            marker.setVisible(false);
            markers.add(marker);

    }
//adds marker and drawableid to the given hashmap
    public void addToMarkerImageHashmap(Map<Marker,Integer> markerAndImage){

        for (Map.Entry element:hashmap.entrySet()){
            Marker marker = (Marker)element.getKey();
            markerAndImage.put(marker, hashmap.get(marker).drawableId);
        }
    }

    public void zoomIn() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomInArea, zoomFactor));
    }

    // Zoom to fit in all markers including current position (calculatemiddlepoint is renamed)
    public void zoomFit(Marker currentposition) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Map.Entry element : hashmap.entrySet()) {
            Marker key = (Marker) element.getKey();
            builder.include(key.getPosition());
        }

        builder.include(currentposition.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //map.moveCamera(cu);
        map.animateCamera(cu);

    }

    // another zoom in function(not used)
    public void zoom() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Map.Entry element : hashmap.entrySet()) {
            Marker key = (Marker) element.getKey();
            builder.include(key.getPosition());
        }

        //builder.include(currentposition.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);

        //map.animateCamera(cu);
    }
    //creating url for json request
    private String getUrl(LatLng origin, LatLng dest, String directionMode, List<LatLng> waypoints) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service

        String str_waypoints = "waypoints=";

        for(int i=0; i<waypoints.size()-1; i++){
            if (i!=waypoints.size()-1){
                str_waypoints=str_waypoints+waypoints.get(i).latitude+","+waypoints.get(i).longitude+"|";
            }
            else str_waypoints = str_waypoints+waypoints.get(i).latitude+","+waypoints.get(i).longitude;
        }

        String parameters = str_origin + "&" + str_dest + "&" + str_waypoints + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBg2CwABbCb-ql9-_YtXA4mGDDI7X1nuU8";
        return url;
    }
    //create array for waypoints
    private List<LatLng> getWaypoints(List<Marker> markers){
        List<LatLng> wayPoints = new ArrayList();
        for(int i=1; i<markers.size()-1; i++){
            wayPoints.add(markers.get(i).getPosition());
        }
        return wayPoints;
    }
    //request
    public void showTrail(Context context){
        new FetchURL(context).execute(getUrl(markers.get(0).getPosition(), markers.get(markers.size()-1).getPosition(), "walking", getWaypoints(markers)), "walking");
    }

}

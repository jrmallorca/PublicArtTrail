package com.publicarttrail.googlemapspractice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    public LatLngBounds.Builder builder = new LatLngBounds.Builder();


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

    public void addMarker(ArtWork artwork, Context context) {

            Marker marker =  map.addMarker(new MarkerOptions().position(artwork.latLng).title(artwork.name).snippet(artwork.artistName).icon(bitmapDescriptorFromVector(context, numbermarker(markers.size()+1)) ));
            hashmap.put(marker, artwork);
            marker.setVisible(false);
            markers.add(marker);
            builder.include(marker.getPosition());


    }
//adds marker and drawableid to the given hashmap
    public void addToMarkerImageHashmap(Map<Marker,Integer> markerAndImage){

        for (Map.Entry element:hashmap.entrySet()){
            Marker marker = (Marker)element.getKey();
            markerAndImage.put(marker, hashmap.get(marker).drawableId);
        }
    }

    public void zoomIn() {
       // map.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomInArea, zoomFactor));
        int padding = 70; // offset from edges of the map in pixels
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);

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

    //function to set icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId){
        Drawable vectorDrawable= ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //return marker depending on position of marker in the list
    private int numbermarker(int i){
        if (i==1) return R.drawable.number_1;
       else if (i==2) return R.drawable.number_2;
        else if (i==3) return R.drawable.number_3;
       else if (i==4) return R.drawable.number_4;
        else if (i==5) return R.drawable.number_5;
        else if (i==6) return R.drawable.number_6;
        else if (i==7) return R.drawable.number_7;
       else if (i==8) return R.drawable.number_8;
        else if (i==9) return R.drawable.number_9;
        else return R.drawable.number_10;
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
    //creating url for json request for trail
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

    //request for current location to trail
    private String getUrl2(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service



        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBg2CwABbCb-ql9-_YtXA4mGDDI7X1nuU8";
        return url;
    }

    //get direction from current location to trail
    public void getDirection(Context context, LatLng currentLocation){
        new FetchURL(context).execute(getUrl2(currentLocation, markers.get(0).getPosition(), "driving"), "driving");
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

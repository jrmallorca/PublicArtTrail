package com.publicarttrail.googlemapspractice.pojo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.publicarttrail.googlemapspractice.R;
import com.publicarttrail.googlemapspractice.directionhelpers.FetchURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// POJO converted from JSON
public class Trail {
    // Base attributes
    private long id;
    private String name;
    private List<Artwork> artworks;

    // More complex attributes for methods
    private GoogleMap map;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void setId(long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setArtworks(List<Artwork> artworks) { this.artworks = artworks; }

    // --- Marker methods ---

    // TODO: 10/02/2020 Hmmm... Dunno how to improve but there might be a better way??? Either replace this or reconsider ordering in DB
    // Return marker depending on position of marker in the list
    public int numberMarker(int i) {
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

    // --- Zoom methods ---

    // TODO: fix zoomIn to show all markers as well as the polyline(trail) in one frame.
    public void zoomIn() {
        int padding = 70; // Offset from edges of the map in pixels
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
    }

    // Zoom to fit in all markers including current position
    public void zoomFit(Marker currentPosition, Map<Artwork, Marker> artworkMarker) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Artwork a : artworks) {
            Marker key = artworkMarker.get(a);
            builder.include(Objects.requireNonNull(key).getPosition());
        }

        builder.include(currentPosition.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 70; // Offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //map.moveCamera(cu);
        map.animateCamera(cu);
    }

    // --- Visibility of trail methods ---

    // Creating URL for JSON request for trail
    public String getURLTrailPath(LatLng origin, LatLng dest, String directionMode, List<LatLng> waypoints) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Mode
        String mode = "mode=" + directionMode;

        // Building the parameters to the web service
        StringBuilder str_waypoints = new StringBuilder("waypoints=");

        for (int i = 0; i <= waypoints.size() - 1; i++) {
            if (i != waypoints.size() - 1) {
                str_waypoints.append(waypoints.get(i).latitude).append(",").append(waypoints.get(i).longitude).append("|");
            } else str_waypoints.append(waypoints.get(i).latitude).append(",").append(waypoints.get(i).longitude);
        }

        String parameters = str_origin + "&" + str_dest + "&" + str_waypoints + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBg2CwABbCb-ql9-_YtXA4mGDDI7X1nuU8";
    }

    // Request for current location to trail
    public String getURLUserPath(LatLng origin, LatLng dest, String directionMode) {
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
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBg2CwABbCb-ql9-_YtXA4mGDDI7X1nuU8";
    }

    // Create array for waypoints
    private List<LatLng> getWaypoints(Map<Artwork, Marker> artworkMarker) {
        List<LatLng> wayPoints = new ArrayList<>();
        for (int i = 1; i < artworks.size() - 1; i++) {
            wayPoints.add(Objects.requireNonNull(artworkMarker.get(artworks.get(i))).getPosition());
        }
        return wayPoints;
    }

    // Request
    public void showTrail(Context context, Map<Artwork, Marker> artworkMarker) {
        new FetchURL(context)
                .execute(getURLTrailPath(Objects.requireNonNull(artworkMarker.get(artworks.get(0))).getPosition(),
                                Objects.requireNonNull(artworkMarker.get(artworks.get(artworks.size() - 1))).getPosition(),
                   "walking",
                                getWaypoints(artworkMarker)),
                         "walking");
    }

    // --- Location methods ---

    // Get direction from current location to trail (still related to trails^^)
    public void getDirection(Context context, LatLng currentLocation, Map<Artwork, Marker> artworkMarker) {
        new FetchURL(context)
                .execute(getURLUserPath(currentLocation,
                                 Objects.requireNonNull(artworkMarker.get(artworks.get(0))).getPosition(),
                    "driving"),
                         "driving");
    }
}

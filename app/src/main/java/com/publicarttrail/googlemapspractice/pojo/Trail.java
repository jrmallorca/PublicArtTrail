package com.publicarttrail.googlemapspractice.pojo;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.publicarttrail.googlemapspractice.R;
import com.publicarttrail.googlemapspractice.directionhelpers.FetchURL;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

// POJO converted from JSON
public class Trail {
    // Base attributes
    private int id;
    private String name;
    private List<TrailArtwork> trailArtworks;

    // More complex attributes for methods
    private GoogleMap map;
    private LatLngBounds.Builder builder;
    private boolean LAT_LNG_BUILT = false;

    private Hashtable<Integer, Artwork> rankArtwork;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<TrailArtwork> getTrailArtworks() {
        return trailArtworks;
    }

    public List<Artwork> getArtworks() {
        List<Artwork> artworks = new ArrayList<>();
        for (TrailArtwork ta : trailArtworks) {
            artworks.add(ta.getArtwork());
        }
        return artworks;
    }

    public Hashtable<Integer, Artwork> getRankArtwork() {
        if (rankArtwork == null) initRankArtwork();
        return rankArtwork;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTrailArtworks(List<TrailArtwork> trailArtworks) {
        this.trailArtworks = trailArtworks;
    }

    public void setRankArtwork(Hashtable<Integer, Artwork> rankArtwork) {
        this.rankArtwork = rankArtwork;
    }

    public Trail(int id,
                 String name,
                 List<TrailArtwork> trailArtworks) {
        this.id = id;
        this.name = name;
        this.trailArtworks = trailArtworks;
    }

    private void initRankArtwork() {
        if (rankArtwork == null) {
            rankArtwork = new Hashtable<>();
            for (TrailArtwork ta : trailArtworks)
                rankArtwork.put(ta.getArtworkRank(), ta.getArtwork());
        }
    }

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

    private void initLatLngBuilder() {
        if (!LAT_LNG_BUILT) {
            builder = new LatLngBounds.Builder();
            for (TrailArtwork ta : trailArtworks) builder.include(ta.getArtwork().getLatLng());
            LAT_LNG_BUILT = true;
        }
    }

    // TODO: fix zoomIn to show all markers as well as the polyline(trail) in one frame.
    public void zoomIn() {
        if (!LAT_LNG_BUILT) initLatLngBuilder();

        int padding = 70; // Offset from edges of the map in pixels
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
    }

    // Zoom to fit in all markers including current position
    public void zoomFit(Marker currentPosition) {
        if (!LAT_LNG_BUILT) initLatLngBuilder();

        LatLngBounds.Builder builder = this.builder;
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

        for (int i = 0; i < waypoints.size(); i++) {
            if (i != waypoints.size() - 1) str_waypoints.append(waypoints.get(i).latitude).append(",").append(waypoints.get(i).longitude).append("|");
            else str_waypoints.append(waypoints.get(i).latitude).append(",").append(waypoints.get(i).longitude);
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
    private List<LatLng> getWaypoints() {
        List<LatLng> wayPoints = new ArrayList<>();

        for (int i = 1; i <= trailArtworks.size(); i++)
            wayPoints.add(Objects.requireNonNull(rankArtwork.get(i)).getLatLng());

        return wayPoints;
    }

    public void showTrail(Context context) {
        if (rankArtwork.isEmpty()) initRankArtwork();

        new FetchURL(context)
                .execute(getURLTrailPath(Objects.requireNonNull(rankArtwork.get(1)).getLatLng(),
                                Objects.requireNonNull(rankArtwork.get(rankArtwork.size())).getLatLng(),
                   "walking",
                                getWaypoints()),
                         "walking");
    }

    // --- Location methods ---

    // Get direction from current location to trail
    public void getDirection(Context context, LatLng currentLocation) {
        new FetchURL(context)
                .execute(getURLUserPath(currentLocation,
                                 Objects.requireNonNull(rankArtwork.get(1)).getLatLng(),
                    "driving"),
                         "driving");
    }
}

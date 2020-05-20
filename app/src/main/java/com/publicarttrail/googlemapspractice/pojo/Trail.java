package com.publicarttrail.googlemapspractice.pojo;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
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
    public PolylineOptions trailPath;
    public PolylineOptions locationPath;


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
            for (TrailArtwork ta : trailArtworks) {
                rankArtwork.put(ta.getArtworkRank(), ta.getArtwork());
                Log.d("logrrank1:", Integer.toString(ta.getArtworkRank()));
            }
        }
    }

    private void initLatLngBuilder() {
        if (builder == null) {
            builder = new LatLngBounds.Builder();
            for (TrailArtwork ta : trailArtworks) {
                Log.d("name:",  ta.getArtwork().getName());
                builder.include(ta.getArtwork().getLatLng());
            }
        }
    }

    // --- Zoom methods ---

    // TODO: fix zoomIn to show all markers as well as the polyline(trail) in one frame.
    public void zoomIn() {
        if (builder == null) initLatLngBuilder();

        int padding = 70; // Offset from edges of the map in pixels
        LatLngBounds bounds = builder.build();

        Log.d("bound:",  bounds.toString());
        Log.d("bound:",  builder.toString());

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
    }

    // Zoom to fit in all markers including current position
    public void zoomFit(Marker currentPosition) {

        LatLngBounds.Builder builderNew = new LatLngBounds.Builder();
        for (TrailArtwork ta : trailArtworks) {
            builderNew.include(ta.getArtwork().getLatLng());
        }
        builderNew.include(currentPosition.getPosition());

        LatLngBounds bounds = builderNew.build();

        int padding = 70; // Offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

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
        if(rankArtwork==null) initRankArtwork();

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
        if(rankArtwork==null) initRankArtwork();

        new FetchURL(context)
                .execute(getURLUserPath(currentLocation,
                        Objects.requireNonNull(rankArtwork.get(1)).getLatLng(),
                        "walking"),
                        "walking");
    }

    public boolean shouldGetDirections(Location currentLocation){
        for(Artwork artwork:getArtworks()){
            Location location = new Location("");
            location.setLatitude(artwork.getLatLng().latitude);
            location.setLongitude(artwork.getLatLng().longitude);
            float distance = currentLocation.distanceTo(location);
            Log.d("mylogrdistance", String.valueOf(distance));

            if (currentLocation.distanceTo(location)<=3000) return true;
        }
        return false;
    }

}

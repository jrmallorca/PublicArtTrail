package com.example.myapplication;

import com.google.android.gms.maps.model.LatLng;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;
import com.publicarttrail.googlemapspractice.pojo.TrailArtwork;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TrailsTest {

    private Trail trail;

    @Before //This is executed before the @Test executes
    public void setUp(){
        Artwork artwork1 = new Artwork(1, "Tyndall Gate", "John",
                "Description", 51.458530, -2.603452, "aa");
        TrailArtwork trailArtwork1 = new TrailArtwork(artwork1, 1);
        List<TrailArtwork> artworks = new ArrayList<>();
        artworks.add(trailArtwork1);

        trail = new Trail(1, "RFG", artworks);
        trail.setId(1);
        trail.setName("RFG");

    }

    @Test
    public void testgetUrl() {
        LatLng origin =new LatLng(51.462773, -2.601686);
        LatLng destination = new LatLng(51.458614, -2.602028);
        String directionMode = "walking";
        List<LatLng>waypoints = new ArrayList<>();
        LatLng waypoint = new LatLng(51.4578, -2.601779);
        waypoints.add(waypoint);

        String url = trail.getURLTrailPath(origin, destination, directionMode, waypoints);

        String check = "https://maps.googleapis.com/maps/api/directions/json?origin=51.462773,-2.601686&destination=51.458614,-2.602028&waypoints=51.4578,-2.601779&mode=walking&key=AIzaSyBg2CwABbCb-ql9-_YtXA4mGDDI7X1nuU8";
        assertEquals("Not equal", check, url);
        //The message here is displayed iff the test fails
    }


    @Test
    public void testgetUrl2() {
        LatLng origin =new LatLng(51.462773, -2.601686);
        LatLng destination = new LatLng(51.458614, -2.602028);
        String directionMode = "walking";

        String url = trail.getURLUserPath(origin, destination, directionMode);

        String check = "https://maps.googleapis.com/maps/api/directions/json?origin=51.462773,-2.601686&destination=51.458614,-2.602028&mode=walking&key=AIzaSyBg2CwABbCb-ql9-_YtXA4mGDDI7X1nuU8";
        assertEquals("Not equal", check, url);
        //The message here is displayed iff the test fails
    }

//    @Test
//    public void testNumberMarker(){
//        int drawable = trail.numberMarker(5);
//        int check = R.drawable.number_5;
//        assertEquals("Not equal", check, drawable);
//    }
}

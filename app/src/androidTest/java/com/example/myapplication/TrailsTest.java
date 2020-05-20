package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;
import com.publicarttrail.googlemapspractice.R;
import com.publicarttrail.googlemapspractice.TrailsActivity;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;
import com.publicarttrail.googlemapspractice.pojo.TrailArtwork;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TrailsTest  {

    private Trail trail;
    LatLng currentLocation = new LatLng(51.457899, -2.603351);


    @Rule
    public ActivityTestRule<TrailsActivity> mActivityRule = new ActivityTestRule<TrailsActivity>(TrailsActivity.class);


    @Before //This is executed before the @Test executes
    public void setUp(){


        Artwork artwork1 = new Artwork(1, "Tyndall Gate", "John",
                "DescriptionTyndall", 51.458530, -2.603452, convert(R.drawable.error_image));
        Artwork artwork2 = new Artwork(2, "Follow Me", "??",
                "DescriptionFollow", 51.457876, -2.602892, convert(R.drawable.follow_me));
        Artwork artwork3 = new Artwork(1, "Goldney Hall", "??",
                "DescriptionGold", 51.452644, -2.615080, convert(R.drawable.error_image));
        TrailArtwork trailArtwork1 = new TrailArtwork(artwork1, 1);
        TrailArtwork trailArtwork2 = new TrailArtwork(artwork2, 2);
        TrailArtwork trailArtwork3 = new TrailArtwork(artwork3, 1);

        List<TrailArtwork> trailArtworks1 = new ArrayList<>();

        trailArtworks1.add(trailArtwork1);
        trailArtworks1.add(trailArtwork2);
        trailArtworks1.add(trailArtwork3);
        trail = new Trail(1, "RFG", trailArtworks1);

    }

    public String convert(int id){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), id);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        //android.util.Base64.encodeToString(byteArrayImage, android.util.Base64.DEFAULT);
        String imageString = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("encode64", imageString);
        return imageString;
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




}

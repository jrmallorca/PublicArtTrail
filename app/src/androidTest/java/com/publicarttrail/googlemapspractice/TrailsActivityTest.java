package com.publicarttrail.googlemapspractice;


import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.DrawerMatchers;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.publicarttrail.googlemapspractice.drawableMatcher.EspressoTestsMatchers;
import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.idlingResource.EventBusIdlingResourceArtwork;
import com.publicarttrail.googlemapspractice.idlingResource.IdlingResourceSleeper;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;
import com.publicarttrail.googlemapspractice.pojo.TrailArtwork;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import androidx.test.InstrumentationTes


@RunWith(AndroidJUnit4.class)
public class TrailsActivityTest {

    static volatile boolean stopThread = false;
    private List<Target> targets = new ArrayList<>(); // Assigns marker icon from Picasso

    //before launching, setup an event bus
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public ActivityTestRule<TrailsActivity> mActivityRule = new ActivityTestRule<TrailsActivity>(TrailsActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            setUp();
        }
    };


    public void setUp(){
        Artwork artwork1 = new Artwork(1, "Tyndall Gate", "John",
                "DescriptionTyndall", 51.458530, -2.603452, convert(R.drawable.error_image));
        Artwork artwork2 = new Artwork(2, "Follow Me", "??",
                "DescriptionFollow", 51.457876, -2.602892, convert(R.drawable.follow_me));
        Artwork artwork3 = new Artwork(3, "Hollow", "??",
                "DescriptionHollow", 51.457470, -2.600915, convert(R.drawable.follow_me));

        Artwork artwork4 = new Artwork(1, "Goldney Hall", "??",
                "DescriptionGold", 51.452644, -2.615080, convert(R.drawable.error_image));
        TrailArtwork trailArtwork1 = new TrailArtwork(artwork1, 1);
        TrailArtwork trailArtwork2 = new TrailArtwork(artwork2, 2);
        TrailArtwork trailArtwork3 = new TrailArtwork(artwork3, 3);
        TrailArtwork trailArtwork4 = new TrailArtwork(artwork4, 1);


        List<TrailArtwork> trailArtworks1 = new ArrayList<>();
        List<TrailArtwork> trailArtworks2 = new ArrayList<>();

        trailArtworks1.add(trailArtwork1);
        trailArtworks1.add(trailArtwork2);
        trailArtworks1.add(trailArtwork3);
        trailArtworks2.add(trailArtwork4);
        Trail trail1 = new Trail(1, "RFG", trailArtworks1);
        Trail trail2 = new Trail(2, "Goldney", trailArtworks2);
        List<Trail> trailList = new ArrayList<>();
        List<Artwork> artworks = new ArrayList<>();
        trailList.add(trail1);
        trailList.add(trail2);
        artworks.add(artwork1);
        artworks.add(artwork2);
        artworks.add(artwork3);
        artworks.add(artwork4);
        TrailAcquiredEvent trailAcquiredEvent = new TrailAcquiredEvent(trailList);
        EventBus.getDefault().postSticky(trailAcquiredEvent);
        ArtworkAcquiredEvent artworkAcquiredEvent = new ArtworkAcquiredEvent(artworks);
        EventBus.getDefault().postSticky(artworkAcquiredEvent);

    }

    @Before
    public void before() {

        mActivityRule.getActivity();


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

    public Boolean isNear(LatLng center, LatLng test){

        // centerloc.
        boolean isWithin100m = distanceTo(center, test) < 100;
        return isWithin100m;
    }

    public float distanceTo(LatLng center, LatLng test){

        Log.d("mylogrlatlng", center.toString());

        Location centerloc = new Location("");
        centerloc.setLatitude(center.latitude);
        centerloc.setLongitude(center.longitude);

        Log.d("mylogrlatlng", test.toString());
        Location testLoc = new Location("");
        testLoc.setLatitude(test.latitude);
        testLoc.setLongitude(test.longitude);


        float distanceInMeters = testLoc.distanceTo(centerloc);
        Log.d("mylogrlatlng", Float.toString(distanceInMeters));

        return distanceInMeters;
    }


    //function for mocking moving user
    public void setUpMovingUser(){
        LatLng startPos = new LatLng(51.457899, -2.603351);
        startUpdates(mActivityRule.getActivity(), new Handler(Looper.getMainLooper()), startPos, 20, 5);
    }

    //function for mocking still user
    public void setUpStillUser(){
        LatLng startPos = new LatLng(51.457899, -2.603351);
        startUpdates(mActivityRule.getActivity(), new Handler(Looper.getMainLooper()), startPos, 20, 0);
    }

    public void setUpFarStillUser(){
        LatLng startPos = new LatLng(51.4700, -0.4543);
        startUpdates(mActivityRule.getActivity(), new Handler(Looper.getMainLooper()), startPos, 20, 0);
    }





    //Test for initial view
    @Test
    public void checkView() {

        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.currentLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    //Tests if marker1 is  present (checks by clicking and ensuring no exception is thrown
    @Test
    public void checkMarker1() throws UiObjectNotFoundException{
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker1 = mDevice.findObject(new UiSelector().descriptionContains("Tyndall Gate"));
        marker1.click();

    }

    //Tests if marker2 is  present (checks by clicking and ensuring no exception is thrown

    @Test
    public void checkMarker2() throws UiObjectNotFoundException, InterruptedException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker2 = mDevice.findObject(new UiSelector().descriptionContains("Follow Me"));
        marker2.click();
    }

    @Test
    public void checkMarker3() throws UiObjectNotFoundException, InterruptedException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker3 = mDevice.findObject(new UiSelector().descriptionContains("Hollow"));
        marker3.click();
    }

    @Test
    public void checkMarker4() throws UiObjectNotFoundException, InterruptedException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker4 = mDevice.findObject(new UiSelector().descriptionContains("Goldney Hall"));
        marker4.click();
    }


    //Tests if marker is updated for user
    @Test
    public void currentLocationForMovingUser()throws UiObjectNotFoundException, InterruptedException{
        setUpMovingUser(); //set up mock location
        onView(withId(R.id.currentLocation)).perform(ViewActions.click()); //click button
        Thread.sleep(2000);
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject currentLocation = mDevice.
                findObject(new UiSelector().descriptionContains("You are here!")); //find current loc marker
        currentLocation.click(); //make sure marker is present (there is no other way to check)
        Rect rects1 = currentLocation.getVisibleBounds(); //get position on screen
        Thread.sleep(2000);

        UiDevice mDevice2 = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject currentLocation2 = mDevice2.
                findObject(new UiSelector().descriptionContains("You are here!"));//find current loc marker after 3s
        Rect rects2 = currentLocation2.getVisibleBounds(); //get position on screen
        stopThread = true; //this will stop the thread thats mocking location so that next tests arent affected
        Assert.assertNotEquals(rects1, rects2); //confirm both positions are not equal
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());//click button to go back to normal screen


    }

    //Tests if marker is updated for user
    @Test
    public void currentLocationForStillUser()throws UiObjectNotFoundException, InterruptedException{
        setUpStillUser(); //set up mock location
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());

        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject currentLocation = mDevice.
                findObject(new UiSelector().descriptionContains("You are here!")); //find current loc marker
        currentLocation.click(); //make sure marker is present (there is no other way to check)
        Rect rects1 = currentLocation.getVisibleBounds(); //get position on screen
        Thread.sleep(3000);

        UiDevice mDevice2 = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject currentLocation2 = mDevice2.
                findObject(new UiSelector().descriptionContains("You are here!"));//find current loc marker after 3s
        Rect rects2 = currentLocation2.getVisibleBounds(); //get position on screen
        stopThread = true; //this will stop the thread thats mocking location so that next tests arent affected
        Assert.assertEquals(rects1, rects2); //confirm both positions are equal
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());//click button to go back to normal screen

    }

    @Test
    public void currentLocationForFarStillUser()throws UiObjectNotFoundException, InterruptedException{
        setUpFarStillUser(); //set up mock location
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());

        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject currentLocation = mDevice.
                findObject(new UiSelector().descriptionContains("You are here!")); //find current loc marker
        currentLocation.click(); //make sure marker is present (there is no other way to check)
        Rect rects1 = currentLocation.getVisibleBounds(); //get position on screen
        Thread.sleep(3000);

        UiDevice mDevice2 = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject currentLocation2 = mDevice2.
                findObject(new UiSelector().descriptionContains("You are here!"));//find current loc marker after 3s
        Rect rects2 = currentLocation2.getVisibleBounds(); //get position on screen
        stopThread = true; //this will stop the thread thats mocking location so that next tests arent affected
        Assert.assertEquals(rects1, rects2); //confirm both positions are equal
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());//click button to go back to normal screen

    }


    @Test
    public void clickOnYourNavigationItem_ShowsYourScreen() throws InterruptedException {
        Thread.sleep(5000); //wait for view to change

        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_artworks));

        Thread.sleep(5000); //wait for view to change

        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.mySpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(withText("Trails")));
        onView(withId(R.id.mySpinner)).perform(ViewActions.click());
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(0).check(matches(withText("All")));
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(1).check(matches(withText("RFG")));
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(2).check(matches(withText("Goldney")));


    }




    //TODO test for picture
    //Tests info window and info page
    //Can't seem to figure out how to test infowindow content
    @Test
    public void infoWindow() throws UiObjectNotFoundException, InterruptedException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker2 = mDevice.findObject(new UiSelector().descriptionContains("Follow Me"));

        marker2.click(); //click on marker 2
        Thread.sleep(1000);  //1s for infowindow to appear

        Rect rects = marker2.getBounds();
        mDevice.click(rects.centerX(), rects.top - 50);//click on infowindow using position
        // obtained from bounds

        Thread.sleep(5000); //wait for click to be registered?

        //wait until eventbus for artwork is posted onto
        EventBusIdlingResourceArtwork eventBusIdlingResourceArtwork = new EventBusIdlingResourceArtwork();
        IdlingRegistry.getInstance().register(eventBusIdlingResourceArtwork);
        IdlingResourceSleeper.sleep(eventBusIdlingResourceArtwork);
        IdlingRegistry.getInstance().unregister(eventBusIdlingResourceArtwork);

        Thread.sleep(5000); //wait for view to change

        onView(withId(R.id.picture)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.follow_me)));
        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Follow Me")));
        onView(withId(R.id.artist)).check(matches(withText("??")));
        onView(withId(R.id.description)).check(matches(withText("DescriptionFollow")));

    }

    /////////////////////////////Tests for RFG trail////////////////////////////////////

    public void selectTrail() throws InterruptedException {
        Thread.sleep(5000); //wait for view to change

        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(1));
        Thread.sleep(2000); //wait for view to change

    }

    @Test
    public void trailView() throws InterruptedException {
        selectTrail();
        checkView();

    }

    @Test
    public void trailCheckMarker1() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        checkMarker1();
    }

    @Test
    public void trailCheckMarker2() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        checkMarker2();
    }

    @Test
    public void trailCheckMarker3() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        checkMarker3();
    }

    @Test
    public void trailCheckTrailPath() throws InterruptedException {
        selectTrail();

        Trail trail = mActivityRule.getActivity().getTrails().get(0);
        List<LatLng> list = trail.trailPath.getPoints();

        Assert.assertTrue(isNear(trail.getTrailArtworks().get(0).getArtwork().getLatLng(), list.get(0)));
        Assert.assertTrue(isNear(trail.getTrailArtworks().get(2).getArtwork().getLatLng(), list.get(list.size()-1)));
        boolean value = false;
        double minimum = Double.POSITIVE_INFINITY;
        for(LatLng latLng:list){
            float distance = distanceTo(trail.getTrailArtworks().get(1).getArtwork().getLatLng(), latLng);
            if(distance<minimum){
                minimum = distance;
            }
        }
        if (minimum<100) value =true;
        Assert.assertTrue(value);
    }

    @Test(expected = UiObjectNotFoundException.class)
    public void trailCheckMarker4() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        checkMarker4();
    }

    @Test
    public void trailCurrentLocationDirections() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        //currentLocationForMovingUser();
        setUpMovingUser(); //set up mock location
        onView(withId(R.id.currentLocation)).perform(ViewActions.click()); //click button
        Thread.sleep(2000);

        Trail trail1 = mActivityRule.getActivity().getTrails().get(0);
        LatLng current1 = mActivityRule.getActivity().getCurrentLoc();
        List<LatLng> list1 = trail1.locationPath.getPoints();
        Assert.assertTrue(isNear(current1, list1.get(0)));
        Assert.assertTrue(isNear(trail1.getTrailArtworks().get(0).getArtwork().getLatLng(), list1.get(list1.size()-1)));

        Thread.sleep(2000);

        Trail trail2 = mActivityRule.getActivity().getTrails().get(0);
        LatLng current2 = mActivityRule.getActivity().getCurrentLoc();
        List<LatLng> list2 = trail2.locationPath.getPoints();
        Assert.assertTrue(isNear(current2, list2.get(0)));
        Assert.assertTrue(isNear(trail2.getTrailArtworks().get(0).getArtwork().getLatLng(), list2.get(list2.size()-1)));

        stopThread = true; //this will stop the thread thats mocking location so that next tests arent affected
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());//click button to go back to normal screen
    }

    @Test
    public void trailCurrentLocationForMovingUser() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        currentLocationForMovingUser();
    }

    @Test
    public void trailCurrentLocationForStillUser() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        currentLocationForStillUser();
    }

    @Test
    public void trailCurrentLocationForFarStillUser() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        setUpFarStillUser(); //set up mock location
        onView(withId(R.id.currentLocation)).perform(ViewActions.click());
        Thread.sleep(2000); //wait for view to change
        onView(withText("Alert")).check(matches(isDisplayed()));
        stopThread = true;
        onView(withText("OK")).perform(ViewActions.click());
        onView(withText("Alert")).check(doesNotExist());
        checkView();

    }

    @Test
    public void trailInfoWindow() throws InterruptedException, UiObjectNotFoundException {
        selectTrail();
        infoWindow();
    }







    //**********************************************************************************************

    //function that mocks location
    public void startUpdates(
            final Activity activity, final Handler mHandler, final LatLng pos,
            final double heading, final double movement) {
        // Log.d("mylogrtest2", Integer.toString(counter));

        Runnable runnable = new Runnable(){
            private LatLng myPos = new LatLng(pos.latitude,pos.longitude);

            @Override
            public void run() {

                if(stopThread==true){
                    // Log.d("mylogrteststop", Integer.toString(counter));
                    stopThread = false;
                    return;}
                // Log.d("mylogrtest", Integer.toString(counter));

                Location mockLocation = new Location(LocationManager.GPS_PROVIDER); // a string
                mockLocation.setLatitude(myPos.latitude);  // double
                mockLocation.setLongitude(myPos.longitude);
                mockLocation.setAltitude(100);
                mockLocation.setTime(System.currentTimeMillis());
                mockLocation.setAccuracy(1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }

                LocationServices.getFusedLocationProviderClient(activity).setMockMode(true);
                LocationServices.getFusedLocationProviderClient(activity).setMockLocation(mockLocation);

                // compute next position
                // (In a test utility class in this example: LocationUtils.java)
                //  Utility - uses SphericalUtil to maintain a position based on
                //  initial starting position, heading and movement value (in
                //   meters) applied every 1 second.
                myPos = SphericalUtil.computeOffset(myPos, movement, heading);
                mHandler.postDelayed(this, 1000);
            }
        };

        mHandler.postDelayed(runnable, 1000);
    }




}

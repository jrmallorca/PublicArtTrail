package com.publicarttrail.googlemapspractice;


import android.Manifest;
import android.app.Activity;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
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
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import org.greenrobot.eventbus.EventBus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TrailsActivityTest {

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
                "Description", 51.458530, -2.603452, "aa");
        Artwork artwork2 = new Artwork(2, "Follow Me", "??",
                "Description", 51.457876, -2.602892, "aa");

        List<Artwork> trail = new ArrayList<>();
        trail.add(artwork1);
        trail.add(artwork2);
        Trail trail1 = new Trail();
        trail1.setId(1);
        trail1.setName("RFG");
        trail1.setArtworks(trail);
        List<Trail> trailList = new ArrayList<>();
        trailList.add(trail1);
        TrailAcquiredEvent trailAcquiredEvent = new TrailAcquiredEvent(trailList);
        EventBus.getDefault().postSticky(trailAcquiredEvent);
    }

    @Before
    public void before() {
        mActivityRule.getActivity();
    }

    //function for mocking moving user
    public void setUpMovingUser(){
        LatLng startPos = new LatLng(51.457899, -2.603351);
        startUpdates(mActivityRule.getActivity(), new Handler(Looper.getMainLooper()),
                startPos, 20, 5);
    }

    //function for mocking still user
    public void setUpStillUser(){
        LatLng startPos = new LatLng(51.457899, -2.603351);
        startUpdates(mActivityRule.getActivity(), new Handler(Looper.getMainLooper()),
                startPos, 20, 0);
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
    public void checkMarker2() throws UiObjectNotFoundException{
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker2 = mDevice.findObject(new UiSelector().descriptionContains("Follow Me"));
        marker2.click();
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

        Assert.assertNotEquals(rects1, rects2); //confirm both positions are not equal
        currentLocation.click(); //click button to go back to normal screen


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


        Assert.assertEquals(rects1, rects2); //confirm both positions are equal
        currentLocation.click(); //click button to go back to normal screen

    }

    @Test
    public void clickOnYourNavigationItem_ShowsYourScreen() throws InterruptedException {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(2));

        Thread.sleep(5000); //wait for view to change

        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.mySpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(withText("Trails")));
        onView(withId(R.id.mySpinner)).perform(ViewActions.click());
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(0).check(matches(withText("All")));
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(1).check(matches(withText("RFG")));

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

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Follow Me")));
        onView(withId(R.id.artist)).check(matches(withText("??")));
        onView(withId(R.id.description)).check(matches(withText("Description")));

    }

    //**********************************************************************************************

    //function that mocks location
    public static void startUpdates(
            final Activity activity, final Handler mHandler, final LatLng pos,
            final double heading, final double movement) {


        mHandler.postDelayed(new Runnable() {
            private LatLng myPos = new LatLng(pos.latitude,pos.longitude);

            @Override
            public void run() {

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
        }, 1000);
    }






}

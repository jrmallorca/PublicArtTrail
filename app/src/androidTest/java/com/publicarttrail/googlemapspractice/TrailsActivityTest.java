package com.publicarttrail.googlemapspractice;


<<<<<<< Updated upstream
=======
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.os.*;
import com.google.maps.android.SphericalUtil;



import androidx.test.core.app.ActivityScenario;
>>>>>>> Stashed changes
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
<<<<<<< Updated upstream

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onIdle;

import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.mockito.Mockito.*;

import com.publicarttrail.googlemapspractice.R;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.networking.RetrofitService;
import com.publicarttrail.googlemapspractice.networking.TrailsClient;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Artwork.*;
import com.publicarttrail.googlemapspractice.pojo.Trail;
import org.greenrobot.eventbus.EventBus;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
=======
import androidx.test.uiautomator.UiDevice;
import androidx.test.InstrumentationRegistry;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;


import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import static androidx.test.espresso.Espresso.onView;

import org.greenrobot.eventbus.EventBus;
>>>>>>> Stashed changes
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
<<<<<<< Updated upstream
import org.mockito.Mockito;
=======

>>>>>>> Stashed changes

import java.util.ArrayList;
import java.util.List;

<<<<<<< Updated upstream
import retrofit2.*;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//@LargeTest
@RunWith(AndroidJUnit4.class)
public class TrailsActivityTest {

    private Artwork artwork1 = new Artwork(1, "TG", "H", "afwa", 2.3, 2.4, "ff");
    private Callback<List<Trail>> trailsCallback = new Callback<List<Trail>>() {
        @Override
        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
            // Cache the trails
            System.out.println("Hello StackOverflow");
            EventBus.getDefault().postSticky(new TrailAcquiredEvent(response.body()));
            //EspressoHandlingResource.decrement();
            // Start TrailsActivity
        }

        @Override
        public void onFailure(Call<List<Trail>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    @Rule
    public ActivityTestRule<TrailsActivity> mActivityTestRule = new ActivityTestRule<>(TrailsActivity.class, true, false);


    @Before
    public void setUp() throws Exception {
        TrailsClient trailsClient = RetrofitService
                .getRetrofit()
                .create(TrailsClient.class);

        trailsClient.getTrails()
                .clone()
                .enqueue(trailsCallback);
=======
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;


import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class TrailsActivityTest {

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


    @Test
    public void view() {
        ff();
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.currentLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        EventBusIdlingResource eventBusIdlingResource = new EventBusIdlingResource();
        IdlingRegistry.getInstance().register(eventBusIdlingResource);
        IdlingResourceSleeper.sleep(eventBusIdlingResource);
        IdlingRegistry.getInstance().unregister(eventBusIdlingResource);
>>>>>>> Stashed changes

    }

    @Test
<<<<<<< Updated upstream
    public void customIntentToStartActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        mActivityTestRule.launchActivity(intent);
        trailsActivityTest();
    }


    //EventBus eventBus = Mockito.mock(EventBus.class);



    public void trailsActivityTest() {
        Assert.assertNotNull(trailsCallback);
        mainActivityTest2();
    }


    public void mainActivityTest2() {
        ViewInteraction button = onView(
                allOf(withId(R.id.currentLocation),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.drawer_layout),
                                        0),
                                2),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
=======
    public void markers() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker1 = mDevice.findObject(new UiSelector().descriptionContains("Tyndall Gate"));
        UiObject marker2 = mDevice.findObject(new UiSelector().descriptionContains("Follow Me"));
        Assert.assertNotNull(marker1);
        Assert.assertNotNull(marker2);
    }

    @Test
    public void infoWindow() throws UiObjectNotFoundException, InterruptedException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker1 = mDevice.findObject(new UiSelector().descriptionContains("Tyndall Gate"));
        Thread.sleep(10000);

        marker1.click();

        Rect rects = marker1.getBounds();
        mDevice.click(rects.centerX(), rects.top - 30);

        EventBusIdlingResource eventBusIdlingResource = new EventBusIdlingResource();
        IdlingRegistry.getInstance().register(eventBusIdlingResource);
        IdlingResourceSleeper.sleep(eventBusIdlingResource);
        IdlingRegistry.getInstance().unregister(eventBusIdlingResource);

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
    }

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
                myPos = SphericalUtil.computeOffset(myPos, movement, heading);
                mHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }
    public void ff(){
        LatLng startPos = new LatLng(51.458530, -2.603452);
        startUpdates(mActivityRule.getActivity(), new Handler(Looper.getMainLooper()),
                startPos, 340, 25);
    }

    // (In a test utility class in this example: LocationUtils.java)
// Utility - uses SphericalUtil to maintain a position based on
//           initial starting position, heading and movement value (in
//           meters) applied every 1 second.  (So a movement value
//           of 25 equates to 25m/s which equates to ~55MPH)



}
>>>>>>> Stashed changes

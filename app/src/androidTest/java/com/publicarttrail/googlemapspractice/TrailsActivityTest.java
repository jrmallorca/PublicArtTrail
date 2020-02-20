package com.publicarttrail.googlemapspractice;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

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

    }

    @Test
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
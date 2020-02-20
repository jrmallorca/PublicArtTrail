package com.publicarttrail.googlemapspractice;

import android.app.MediaRouteButton;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.intent.Intents.intended;


import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.publicarttrail.googlemapspractice.EspressoTestsMatchers.withDrawable;
import static com.publicarttrail.googlemapspractice.EspressoTestsMatchers.hasDrawable;



@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {


    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<>(MainActivity.class);


    @Test
    public void testForPicture() {
        // wait for Cast button
        onView(withId(R.id.logo)).check(matches(withDrawable(R.drawable.welcome)));
        onView(withId(R.id.logo)).check(matches(hasDrawable()));
        IdlingRegistry.getInstance().register(EspressoHandlingResource.getIdlingResource());
        intended(hasComponent(TrailsActivity.class.getName()));



    }

    //TODO: create test for intent
}

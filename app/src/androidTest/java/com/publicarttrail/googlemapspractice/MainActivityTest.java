package com.publicarttrail.googlemapspractice;

<<<<<<< Updated upstream
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

=======
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setup(){

        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void view(){
        onView(withId(R.id.main)).check(matches(isDisplayed()));
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }

    @Test
    public void goesToNext(){
        EventBusIdlingResource2 eventBusIdlingResource = new EventBusIdlingResource2();
        IdlingRegistry.getInstance().register(eventBusIdlingResource);
        IdlingResourceSleeper.sleep(eventBusIdlingResource);
        IdlingRegistry.getInstance().unregister(eventBusIdlingResource);
        //onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
      //  intended(hasComponent(TrailsActivity.class.getName()));
>>>>>>> Stashed changes


    }

<<<<<<< Updated upstream
    //TODO: create test for intent
}
=======
}
>>>>>>> Stashed changes

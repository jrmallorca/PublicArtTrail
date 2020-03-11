package com.publicarttrail.googlemapspractice;

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

    //Test if view is correct
    @Test
    public void view(){
        onView(withId(R.id.main)).check(matches(isDisplayed()));
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }

    //Test if activity changes correctly. First waits (using databaseIdlingResource )
    // for the database to be uploaded. Then waits for the eventbus to post
    // (using eventbusidlingresouncetrail).

    @Test
    public void goesToNext() throws InterruptedException{

        DatabaseIdlingResource databaseIdlingResource = new DatabaseIdlingResource();
        IdlingRegistry.getInstance().register(databaseIdlingResource);
        IdlingResourceSleeper.sleep(databaseIdlingResource);
        IdlingRegistry.getInstance().unregister(databaseIdlingResource);

        Thread.sleep(5000);

        EventBusIdlingResourceTrail eventBusIdlingResource = new EventBusIdlingResourceTrail();
        IdlingRegistry.getInstance().register(eventBusIdlingResource);
        IdlingResourceSleeper.sleep(eventBusIdlingResource);
        IdlingRegistry.getInstance().unregister(eventBusIdlingResource);

        Thread.sleep(5000);             //wait for view to change

        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));

    }

}

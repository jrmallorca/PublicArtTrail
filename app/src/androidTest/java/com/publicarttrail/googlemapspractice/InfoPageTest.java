package com.publicarttrail.googlemapspractice;


import android.graphics.Rect;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.InstrumentationRegistry;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;


import com.publicarttrail.googlemapspractice.events.ArtworkEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import static androidx.test.espresso.Espresso.onView;

import org.greenrobot.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;


import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class InfoPageTest {


    @Before
    public void setUp(){
        Artwork artwork1 = new Artwork(1, "Tyndall Gate", "John",
                "Description", 51.458530, -2.603452, "aa");


        ArtworkEvent artworkEvent = new ArtworkEvent(artwork1);
        EventBus.getDefault().postSticky(artworkEvent);
        ActivityScenario.launch(InfoPage.class);


    }

    @Test
    public void view() {

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Tyndall Gate")));
        onView(withId(R.id.artist)).check(matches(withText("John")));
        onView(withId(R.id.description)).check(matches(withText("Description")));



    }


}

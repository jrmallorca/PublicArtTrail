package com.publicarttrail.googlemapspractice;


import androidx.test.core.app.ActivityScenario;
import androidx.test.runner.AndroidJUnit4;

import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class InfoPageTest {

    //before launching, setup an event bus and post this
    @Before
    public void setUp(){
        Artwork artwork1 = new Artwork(1, "Tyndall Gate", "John",
                "Description", 51.458530, -2.603452, "aa");


        ArtworkAcquiredEvent artworkEvent = new ArtworkAcquiredEvent(artwork1);
        EventBus.getDefault().postSticky(artworkEvent);
        ActivityScenario.launch(InfoPage.class);


    }

    //Test view
    //TODO picture testing
    @Test
    public void view() {

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Tyndall Gate")));
        onView(withId(R.id.artist)).check(matches(withText("John")));
        onView(withId(R.id.description)).check(matches(withText("Description")));



    }


}

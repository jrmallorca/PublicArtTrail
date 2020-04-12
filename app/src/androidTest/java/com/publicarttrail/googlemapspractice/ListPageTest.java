package com.publicarttrail.googlemapspractice;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.runner.AndroidJUnit4;

import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import org.greenrobot.eventbus.EventBus;
import org.hamcrest.Matchers;
import org.junit.Before;
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
import static com.publicarttrail.googlemapspractice.TestUtils.withRecyclerView;


@RunWith(AndroidJUnit4.class)
public class ListPageTest {

    @Before

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
        ActivityScenario.launch(ListPage.class);

    }

    @Test
    public void viewTest() throws InterruptedException {
        // Open Drawer to click on navigation.
        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.mySpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(withText("Trails")));
    }

    @Test
    public void spinnerTest() {

        onView(withId(R.id.mySpinner)).perform(ViewActions.click());
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(0).check(matches(withText("All")));
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(1).check(matches(withText("RFG")));

        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(1).perform(ViewActions.click());

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artname)).check(matches(withText("Tyndall Gate")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artartist)).check(matches(withText("John")));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artname)).check(matches(withText("Follow Me")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artartist)).check(matches(withText("??")));
    }

    @Test
    public void goesToNextActivityTest() throws InterruptedException {
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artname)).check(matches(withText("Tyndall Gate")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artartist)).check(matches(withText("John")));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.nextArrow)).perform(ViewActions.click());
        Thread.sleep(5000); //wait for view to change

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Tyndall Gate")));
        onView(withId(R.id.artist)).check(matches(withText("John")));
        onView(withId(R.id.description)).check(matches(withText("Description")));
    }



}

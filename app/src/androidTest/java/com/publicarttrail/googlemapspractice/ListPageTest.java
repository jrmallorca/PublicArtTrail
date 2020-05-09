package com.publicarttrail.googlemapspractice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.runner.AndroidJUnit4;

import com.publicarttrail.googlemapspractice.drawableMatcher.EspressoTestsMatchers;
import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;
import com.publicarttrail.googlemapspractice.pojo.TrailArtwork;

import org.greenrobot.eventbus.EventBus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.publicarttrail.googlemapspractice.recyclerView.TestUtils.withRecyclerView;


@RunWith(AndroidJUnit4.class)
public class ListPageTest {

    @Before

    public void setUp(){
        Artwork artwork1 = new Artwork(1, "Tyndall Gate", "John",
                "DescriptionTyndall", 51.458530, -2.603452, convert(R.drawable.error_image));
        Artwork artwork2 = new Artwork(2, "Follow Me", "??",
                "DescriptionFollow", 51.457876, -2.602892, convert(R.drawable.follow_me));
        Artwork artwork3 = new Artwork(1, "Goldney Hall", "??",
                "DescriptionGold", 51.452644, -2.615080, convert(R.drawable.error_image));
        TrailArtwork trailArtwork1 = new TrailArtwork(artwork1, 1);
        TrailArtwork trailArtwork2 = new TrailArtwork(artwork2, 2);
        TrailArtwork trailArtwork3 = new TrailArtwork(artwork3, 1);

        List<TrailArtwork> trailArtworks1 = new ArrayList<>();
        List<TrailArtwork> trailArtworks2 = new ArrayList<>();

        trailArtworks1.add(trailArtwork1);
        trailArtworks1.add(trailArtwork2);
        trailArtworks2.add(trailArtwork3);
        Trail trail1 = new Trail(1, "RFG", trailArtworks1);
        Trail trail2 = new Trail(2, "Goldney", trailArtworks2);
        List<Trail> trailList = new ArrayList<>();
        List<Artwork> artworks = new ArrayList<>();
        trailList.add(trail1);
        trailList.add(trail2);
        artworks.add(artwork1);
        artworks.add(artwork2);
        artworks.add(artwork3);
        TrailAcquiredEvent trailAcquiredEvent = new TrailAcquiredEvent(trailList);
        EventBus.getDefault().postSticky(trailAcquiredEvent);
        ArtworkAcquiredEvent artworkAcquiredEvent = new ArtworkAcquiredEvent(artworks);
        EventBus.getDefault().postSticky(artworkAcquiredEvent);
        ActivityScenario.launch(ListPage.class);

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



    @Test
    public void viewTest() throws InterruptedException {
        // Open Drawer to click on navigation.
        onView(withId(R.id.list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.mySpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(isDisplayed()));
        onView(withId(R.id.trail)).check(matches(withText("Trails")));

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artname)).check(matches(withText("Tyndall Gate")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artartist)).check(matches(withText("John")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));


        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artname)).check(matches(withText("Follow Me")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artartist)).check(matches(withText("??")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.follow_me)));


        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(2, R.id.artname)).check(matches(withText("Goldney Hall")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(2, R.id.artartist)).check(matches(withText("??")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(2, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));



    }

    @Test
    public void spinnerTest() {

        onView(withId(R.id.mySpinner)).perform(ViewActions.click());
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(0).check(matches(withText("All")));
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(1).check(matches(withText("RFG")));
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(2).check(matches(withText("Goldney")));


        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(1).perform(ViewActions.click());

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artname)).check(matches(withText("Tyndall Gate")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artartist)).check(matches(withText("John")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));


        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artname)).check(matches(withText("Follow Me")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artartist)).check(matches(withText("??")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(1, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.follow_me)));

        onView(withId(R.id.mySpinner)).perform(ViewActions.click());

        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(String.class)))).atPosition(2).perform(ViewActions.click());

        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artname)).check(matches(withText("Goldney Hall")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artartist)).check(matches(withText("??")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));


    }

    @Test
    public void goesToNextActivityTest() throws InterruptedException {
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artname)).check(matches(withText("Tyndall Gate")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artartist)).check(matches(withText("John")));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.artpic)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));


        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.nextArrow)).perform(ViewActions.click());
        Thread.sleep(5000); //wait for view to change

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Tyndall Gate")));
        onView(withId(R.id.artist)).check(matches(withText("John")));
        onView(withId(R.id.description)).check(matches(withText("DescriptionTyndall")));
        onView(withId(R.id.picture)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));

    }



}

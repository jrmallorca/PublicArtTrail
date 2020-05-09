package com.publicarttrail.googlemapspractice;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.runner.AndroidJUnit4;

import com.publicarttrail.googlemapspractice.drawableMatcher.EspressoTestsMatchers;
import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
                "Description", 51.458530, -2.603452, convert(R.drawable.error_image));

        ArrayList<Artwork> artworks = new ArrayList<>();
        artworks.add(artwork1);
        ArtworkAcquiredEvent artworkEvent = new ArtworkAcquiredEvent(artworks);
        EventBus.getDefault().postSticky(artworkEvent);
        ActivityScenario.launch(InfoPage.class);


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

    //Test view
    //TODO picture testing
    @Test
    public void view() {

        onView(withId(R.id.info_page)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(withText("Tyndall Gate")));
        onView(withId(R.id.artist)).check(matches(withText("John")));
        onView(withId(R.id.description)).check(matches(withText("Description")));
        onView(withId(R.id.picture)).check(matches(EspressoTestsMatchers.withDrawable(R.drawable.error_image)));


    }


}

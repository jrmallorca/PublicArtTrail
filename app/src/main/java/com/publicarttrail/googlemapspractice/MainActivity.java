package com.publicarttrail.googlemapspractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.networking.BackEndAPI;
import com.publicarttrail.googlemapspractice.networking.RetrofitService;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static io.reactivex.rxjava3.core.Observable.combineLatest;

// TODO: 16/04/2020 Change so that artwork and client finish then change to different activity 
// Start page
public class MainActivity extends AppCompatActivity {
    private ImageView logo;

    // Create the clients that calls HTTP requests
    BackEndAPI backEndAPI = RetrofitService
            .getRetrofit()
            .create(BackEndAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set logo
        logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.welcome);

        // Make 2 asynchronous calls, do something with results, then start new activity
        combineLatest(backEndAPI.getTrails(), backEndAPI.getArtworks(), (trails, artworks) -> {
            // Cache the results
            EventBus.getDefault().postSticky(new TrailAcquiredEvent(trails));
            EventBus.getDefault().postSticky(new ArtworkAcquiredEvent(artworks));

            // Signal that we've finished caching, next observer will receive the Object
            return new Object();
        })
        .subscribeOn(Schedulers.io()) // Where the request is processed. If omitted the computation is done on current thread.
        .observeOn(AndroidSchedulers.mainThread()) // Results are emitted on the specified thread - in this case the UI thread.
        .subscribe( // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
            o -> { // Start TrailsActivity on successful completion of all requests
                Intent info = new Intent(MainActivity.this, TrailsActivity.class);
                startActivity(info);
                finish();
            },

            // Will be triggered if any error during requests will happen
            Throwable::printStackTrace
        );
    }
}

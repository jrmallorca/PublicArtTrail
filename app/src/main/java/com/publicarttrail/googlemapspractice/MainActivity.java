package com.publicarttrail.googlemapspractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.networking.BackEndAPI;
import com.publicarttrail.googlemapspractice.networking.RetrofitService;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Start page
public class MainActivity extends AppCompatActivity {
    private ImageView logo;

    // Create the client that calls HTTP requests
    BackEndAPI client = RetrofitService
            .getRetrofit()
            .create(BackEndAPI.class);

    // Get the result from our GET request
    private Callback<List<Trail>> trailsCallback = new Callback<List<Trail>>() {
        @Override
        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
            List<Trail> trails = response.body();
            List<Artwork> artworks = new ArrayList<>();

            // Get artworks from each trail
            for (Trail t : Objects.requireNonNull(trails)) {
                artworks.addAll(t.getArtworks());
            }

            // Cache the trails
            EventBus.getDefault().postSticky(new TrailAcquiredEvent(trails));
            EventBus.getDefault().postSticky(new ArtworkAcquiredEvent(artworks));

            // Start TrailsActivity
            Intent info = new Intent(MainActivity.this, TrailsActivity.class);
            startActivity(info);
            finish();
        }

        @Override
        public void onFailure(Call<List<Trail>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set logo
        logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.welcome);

        // Call GET request
        client.getTrails().enqueue(trailsCallback);
    }
}


package com.publicarttrail.googlemapspractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.networking.RetrofitService;
import com.publicarttrail.googlemapspractice.networking.TrailsClient;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Start page
public class MainActivity extends AppCompatActivity {
    private ImageView logo;

    // Create the client that calls HTTP requests
    TrailsClient trailsClient = RetrofitService
            .getRetrofit()
            .create(TrailsClient.class);

    // Get the result from our GET request
    private Callback<List<Trail>> trailsCallback = new Callback<List<Trail>>() {
        @Override
        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
            // Cache the trails
            EventBus.getDefault().postSticky(new TrailAcquiredEvent(response.body()));

            // Start TrailsActivity
            Intent info = new Intent(MainActivity.this, TrailsActivity.class);
            startActivity(info);
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
        trailsClient.getTrails()
                .clone()
                .enqueue(trailsCallback);
    }
}

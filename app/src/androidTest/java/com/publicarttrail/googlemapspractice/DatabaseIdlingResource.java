package com.publicarttrail.googlemapspractice;

import android.util.Log;

import androidx.test.espresso.IdlingResource;

import com.publicarttrail.googlemapspractice.networking.RetrofitService;
import com.publicarttrail.googlemapspractice.networking.TrailsClient;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseIdlingResource implements IdlingResource{
    private boolean databaseUpdated;
    private boolean busUpdated;
    private volatile ResourceCallback resourceCallback;
    // Create the client that calls HTTP requests
    TrailsClient trailsClient = RetrofitService
            .getRetrofit()
            .create(TrailsClient.class);


    public DatabaseIdlingResource() {
        databaseUpdated = false;
        // busUpdated = false;
        trailsClient.getTrails()
                .clone()
                .enqueue(trailsCallback);
        //EventBus.getDefault().register(this);

    }
    /*@Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(TrailAcquiredEvent event) {
        busUpdated = true;
        Log.d("mylogr",  "event done");
        EventBus.getDefault().removeStickyEvent(event);
    }*/

    private Callback<List<Trail>> trailsCallback = new Callback<List<Trail>>() {
        @Override
        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
            // Cache the trails
            databaseUpdated = true;
            Trail trail = response.body().get(0);
            Log.d("mylogr",  "database done"+trail.getName());

        }

        @Override
        public void onFailure(Call<List<Trail>> call, Throwable t) {
            t.printStackTrace();
        }
    };


    @Override
    public String getName() {
        return "name";
    }

    @Override
    public boolean isIdleNow() {
        //if (busUpdated) {
        //  EventBus.getDefault().unregister(this);
        // resourceCallback.onTransitionToIdle();

        //}
        // if (databaseUpdated) {
        //EventBus.getDefault().unregister(this);
        // resourceCallback.onTransitionToIdle();

        //}

        //return (databaseUpdated&&busUpdated);
        return databaseUpdated;
    }


    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;

    }
}

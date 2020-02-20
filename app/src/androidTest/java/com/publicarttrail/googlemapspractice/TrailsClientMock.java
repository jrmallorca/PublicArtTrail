package com.publicarttrail.googlemapspractice;

import com.publicarttrail.googlemapspractice.networking.TrailsClient;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class TrailsClientMock implements TrailsClient {

    private final BehaviorDelegate<TrailsClient> delegate;

    public TrailsClientMock(BehaviorDelegate<TrailsClient> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<List<Trail>> getTrails() {
        return delegate.returningResponse("test").getTrails();

    }

    @Override
    public Call<List<Trail>> getTrail(long id) {
        return null;
    }
}

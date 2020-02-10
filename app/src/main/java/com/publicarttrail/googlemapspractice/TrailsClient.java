package com.publicarttrail.googlemapspractice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrailsClient {
    @GET("/trails")
    Call<List<BaseTrail>> getTrails();

    @GET("/trails/{id}")
    Call<List<BaseTrail>> getTrail(@Path("id") long id);
}

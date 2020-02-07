package com.publicarttrail.googlemapspractice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrailsClient {
    @GET("/trails")
    Call<List<Trailv2>> getTrails();

    @GET("/trails/{id}")
    Call<List<Trailv2>> getTrail(@Path("id") long id);
}

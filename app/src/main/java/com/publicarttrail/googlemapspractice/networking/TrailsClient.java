package com.publicarttrail.googlemapspractice.networking;

import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrailsClient {
    @GET("/trails")
    Call<List<Trail>> getTrails();

    @GET("/trails/{id}")
    Call<List<Trail>> getTrail(@Path("id") long id);
}

package com.publicarttrail.googlemapspractice.networking;

import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BackEndAPI {
    @GET("/trails")
    Call<List<Trail>> getTrails();

    @GET("/trails/{id}")
    Call<Trail> getTrail(@Path("id") long id);

    @GET("/artworks")
    Call<List<Artwork>> getArtworks();

    @GET("/artworks/{id}")
    Call<Artwork> getArtwork(@Path("id") long id);
}

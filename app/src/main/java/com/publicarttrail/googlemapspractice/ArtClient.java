package com.publicarttrail.googlemapspractice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArtClient {
    @GET("/artworks")
    Call<List<ArtWorkv2>> getArtworks();

    @GET("/artworks/{id}")
    Call<List<ArtWorkv2>> getArtwork(@Path("id") long id);
}

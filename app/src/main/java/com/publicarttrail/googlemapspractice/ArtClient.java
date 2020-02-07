package com.publicarttrail.googlemapspractice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArtClient {
    @GET("/artworks")
    Call<List<ArtWork>> getArtworks();

    @GET("/artworks/{id}")
    Call<List<ArtWork>> getArtwork(@Path("id") long id);
}

package com.publicarttrail.googlemapspractice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArtClient {
    @GET("/artworks")
    Call<List<BaseArtwork>> getArtworks();

    @GET("/artworks/{id}")
    Call<List<BaseArtwork>> getArtwork(@Path("id") long id);
}

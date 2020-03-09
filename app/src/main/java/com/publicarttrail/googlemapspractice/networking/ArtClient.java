package com.publicarttrail.googlemapspractice.networking;

import com.publicarttrail.googlemapspractice.pojo.Artwork;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArtClient {
    @GET("/artworks")
    Call<List<Artwork>> getArtworks();

    @GET("/artworks/{id}")
    Call<List<Artwork>> getArtwork(@Path("id") long id);
}

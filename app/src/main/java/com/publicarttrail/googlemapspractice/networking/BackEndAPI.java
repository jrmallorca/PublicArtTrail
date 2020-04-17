package com.publicarttrail.googlemapspractice.networking;

import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BackEndAPI {
    @GET("/trails")
    Observable<List<Trail>> getTrails();

    @GET("/trails/{id}")
    Observable<Trail> getTrail(@Path("id") long id);

    @GET("/artworks")
    Observable<List<Artwork>> getArtworks();

    @GET("/artworks/{id}")
    Observable<Artwork> getArtwork(@Path("id") long id);
}

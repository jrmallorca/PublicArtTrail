package com.publicarttrail.googlemapspractice.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton class to make a single global instance of a retrofit
public class RetrofitService {
    // Instance eagerly created so as to guarantee only one instance
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://public-art-trail.herokuapp.com")  // Base URL
            .addConverterFactory(GsonConverterFactory.create()) // Converting JSON objects to POJO
            .build();

    // Private constructor so nothing uses it but the method below
    private RetrofitService() {}

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
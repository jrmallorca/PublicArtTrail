package com.publicarttrail.googlemapspractice;

import java.util.List;

public class Trailv2 {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private List<ArtWorkv2> artworks;

    public Trailv2() {}

    // Custom constructor when an instance is to be created but we don't have an id
    public Trailv2(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<ArtWorkv2> getArtworks() {
        return artworks;
    }
}

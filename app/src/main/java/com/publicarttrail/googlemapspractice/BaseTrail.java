package com.publicarttrail.googlemapspractice;

import java.util.List;

public class BaseTrail {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private List<BaseArtwork> artworks;

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

    public List<BaseArtwork> getArtworks() {
        return artworks;
    }
}

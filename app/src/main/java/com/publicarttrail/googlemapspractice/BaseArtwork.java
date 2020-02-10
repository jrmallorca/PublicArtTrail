package com.publicarttrail.googlemapspractice;

public class BaseArtwork {
    private long id;
    private String name;
    private String creator;
    private String description;
    private double latitude;
    private double longitude;
    // private Image image;

    public BaseArtwork() {}

    // Custom constructor when an instance is to be created but we don't have an id
    public BaseArtwork(String name, String creator, String description, double latitude, double longitude) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

package com.publicarttrail.googlemapspractice;

public class ArtWorkv2 {
    private long id;
    private String name;
    private String creator;
    private String description;
    private double latitude;
    private double longitude;
    // private Image image;
    private Trail trail;

    public ArtWorkv2() {}

    // Custom constructor when an instance is to be created but we don't have an id
    public ArtWorkv2(String name, String creator, String description, double latitude, double longitude, Trail trail) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.trail = trail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Trail getTrail() {
        return trail;
    }

    public void setTrail(Trail trail) {
        this.trail = trail;
    }
}

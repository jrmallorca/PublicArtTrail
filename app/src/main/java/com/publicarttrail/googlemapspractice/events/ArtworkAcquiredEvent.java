package com.publicarttrail.googlemapspractice.events;

import com.publicarttrail.googlemapspractice.pojo.Artwork;

import java.util.List;

// Event to post list of artworks
public class ArtworkAcquiredEvent {
    public final List<Artwork> artworks;

    public ArtworkAcquiredEvent(List<Artwork> artworks) {
        this.artworks = artworks;
    }
}

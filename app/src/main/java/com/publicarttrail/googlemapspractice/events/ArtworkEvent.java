package com.publicarttrail.googlemapspractice.events;

import com.publicarttrail.googlemapspractice.pojo.Artwork;

public class ArtworkEvent {
    public final Artwork artwork;

    public ArtworkEvent(Artwork artwork) {
        this.artwork = artwork;
    }
}

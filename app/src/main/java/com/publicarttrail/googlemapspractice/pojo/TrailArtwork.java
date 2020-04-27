package com.publicarttrail.googlemapspractice.pojo;

public class TrailArtwork {
    private Trail trail;

    private Artwork artwork;

    private int artworkRank;

    public TrailArtwork(Artwork artwork, int artworkRank) {
        this.artwork = artwork;
        this.artworkRank = artworkRank;
    }

    public Trail getTrail() {
        return trail;
    }

    public void setTrail(Trail trail) {
        this.trail = trail;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public int getArtworkRank() {
        return artworkRank;
    }

    public void setArtworkRank(int artworkRank) {
        this.artworkRank = artworkRank;
    }
}

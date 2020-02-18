package com.publicarttrail.googlemapspractice.events;

import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;

public class TrailAcquiredEvent {
    public final List<Trail> trails;

    public TrailAcquiredEvent(List<Trail> trails) {
        this.trails = trails;
    }
}

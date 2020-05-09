package com.publicarttrail.googlemapspractice.idlingResource;

import androidx.test.espresso.IdlingResource;

import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
public class EventBusIdlingResourceArtwork implements IdlingResource{
    private boolean busUpdated;
    private volatile ResourceCallback resourceCallback;


    public EventBusIdlingResourceArtwork() {
        busUpdated = false;
        EventBus.getDefault().register(this);
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ArtworkAcquiredEvent event) {
        busUpdated = true;
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public boolean isIdleNow() {
        if (busUpdated) {
            EventBus.getDefault().unregister(this);
           // resourceCallback.onTransitionToIdle();

        }

        return busUpdated;
    }


    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;

    }
}
package com.publicarttrail.googlemapspractice;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.publicarttrail.googlemapspractice.events.ArtworkEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class InfoPage extends AppCompatActivity {
    private TextView nameText;
    private TextView artistText;
    private TextView descriptionText;
    private ImageView picture;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        // Setting up various activity components
        nameText = findViewById(R.id.name);
        artistText = findViewById(R.id.artist);
        descriptionText = findViewById(R.id.description);
        picture = findViewById(R.id.picture);
        toolbar = findViewById(R.id.toolbar);

        // TODO: 18/02/2020 Might see if we can make toolbar global w/o copy/paste this
        // Setting up toolbar as action bar
        setSupportActionBar(toolbar);
    }

    // Register this activity as a subscriber for events
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    // Unregister this activity as a subscriber for events
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBus.getDefault().removeStickyEvent(ArtworkEvent.class);
    }

    // Called when a TrailAcquiredEvent has been posted
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ArtworkEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);

        Artwork artwork = event.artwork;

        setTitle(artwork.getName());
        nameText.setText(artwork.getName());
        artistText.setText(artwork.getCreator());
        descriptionText.setText(artwork.getDescription());
        picture.setImageBitmap(artwork.getBitmap());
    }
}

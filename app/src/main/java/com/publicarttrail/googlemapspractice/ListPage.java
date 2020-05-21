package com.publicarttrail.googlemapspractice;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.listAdaptor.CustomListAdaptor;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListPage extends AppCompatActivity {
    RecyclerView recyclerView;
    Spinner mySpinner;
    ArrayList<Trail>trails = new ArrayList<>();
    ArrayList<String>itemSelections = new ArrayList<>();
    CustomListAdaptor adapter;
    Map<String, List<Artwork>> artworkMap = new HashMap<>();

    private ArrayList<Artwork> getArtworks(ArrayList<Trail>trails) {
        ArrayList<Artwork> artworks = new ArrayList<>();
        for (Trail trail: trails) {
            for(Artwork trailArtwork:trail.getArtworks()) {
                addIfNotPresent(artworks, trailArtwork);
            }
        }
        return artworks;
    }

    private void addIfNotPresent(ArrayList<Artwork> artworks, Artwork artworkToBeAdded) {
        for (Artwork artwork:artworks)
            if (artwork.getName().equals(artworkToBeAdded.getName())) return;

        artworks.add(artworkToBeAdded);
    }

    private void initializeViews() {
        itemSelections.add("All");
        for (Trail trail : trails) {
            itemSelections.add(trail.getName());
            artworkMap.put(trail.getName(), trail.getArtworks());
        }
        mySpinner = findViewById(R.id.mySpinner);
        mySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemSelections));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new CustomListAdaptor(getArtworks(trails), ListPage.this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //spinner selection events
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemID) {
                if (position >= 0 && position < itemSelections.size()) {
                    getSelectedCategoryData(position);
                } else {
                    Toast.makeText(ListPage.this, "Selected Category Does not Exist!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getSelectedCategoryData(int categoryID) {
        if(categoryID == 0) //All
            adapter = new CustomListAdaptor(getArtworks(trails), ListPage.this);
        else {
            //arraylist to hold selected cosmic bodies
            //filter by id
            ArrayList<Artwork> artworks = new ArrayList<>(Objects.requireNonNull(artworkMap.get((itemSelections.get(categoryID)))));
            //instatiate adapter a
            adapter = new CustomListAdaptor(artworks, ListPage.this);
        }

        //set the adapter to GridView
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        EventBus.getDefault().register(this);

    }

    // Register this activity as a subscriber for events
    @Override
    public void onStart() {
        super.onStart();
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
        EventBus.getDefault().unregister(this);
    }

    // Called when a TrailAcquiredEvent has been posted
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(TrailAcquiredEvent event) {
        trails.addAll(event.trails);
        initializeViews();
    }
}

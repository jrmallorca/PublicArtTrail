package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.publicarttrail.googlemapspractice.directionhelpers.LocationService;
import com.publicarttrail.googlemapspractice.directionhelpers.TaskLoadedCallback;
import com.publicarttrail.googlemapspractice.events.ArtworkEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrailsActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {
    private GoogleMap mMap;

    // Navigation menu attributes
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private static final int Request_Code = 101;
    private Button currentLocationButton;
    private Marker currentLocationMarker = null;
    private Boolean isCurrentLocSet;
    private Polyline trailPolyline;
    private Polyline locationPolyline;
    private Boolean askingForDirection = false;
    private Intent intent;
    private int counter=-1;
    private Boolean isPolylineForTrail = true;

    // Selecting trails attributes
    private List<Trail> trails = new ArrayList<>();
    // TODO: 09/02/2020 Possibility to replace this with id from Trail???
    private Trail trailSelected;

    // -- ACTIVITY RELATED METHODS --

    // Starts off the map Activity and relevant location stuff, also creates the buttons and textview
    // needed which are initially set to be invisible
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trails);

        // Setting up the toolbar we created as the actionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up the drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Attribute that knows dimensions of screen (Used so global map won't appear at beginning)
        drawer.getViewTreeObserver().addOnGlobalLayoutListener(() -> trailSelected.zoomIn());

        // Setting up the hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        // This accounts for rotation, opening app again, etc.
        if (savedInstanceState == null) {
            // Set first trail (Royal Fort Gardens) as selected item
            navigationView.setCheckedItem(0);
        }

        // Setting up location
        isCurrentLocSet = false;

        // Create the buttons
        createButtons();
        EventBus.getDefault().register(this);
        Log.d("debugon", "createcalled");



    }

    // Register this activity as a subscriber for events
    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
        Log.d("debugon", "startcalled");


    }


    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);
        Log.d("debugon", "resumecalled");


    }
    @Override
    public void onPause(){
        EventBus.getDefault().unregister(this);
        super.onPause();
        Log.d("debugon", "pausecalled");

        // EventBus.getDefault().removeStickyEvent(TrailAcquiredEvent.class);
    }

    // Unregister this activity as a subscriber for events
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        Log.d("debugon", "stopcalled");

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Log.d("debugon", "destroycalled");

    }

    // When the map is ready, add markers for all trails, sets current location, and creates a listener
    // for any marker selection
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Menu menu = navigationView.getMenu();
        // Set map for all trails
        for (Trail t : trails) {
            t.setMap(mMap);

            // Add menu item for each trail
            menu.add(R.id.nav_trails_group, (int) t.getId(), Menu.NONE, t.getName());

            // Add marker for each artwork in each trail

            for (Artwork a : t.getArtworks()) {
                t.addMarker(a, TrailsActivity.this);
            }
            menu.add(R.id.nav_trails_group, trails.size()+1, Menu.NONE, "List View");
        }

        //custom infowindow set up (check newly created class)
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(TrailsActivity.this, trails);

        //infowindows in this map will use format set in CustomInfoWindowAdapter
        mMap.setInfoWindowAdapter(adapter);

        //set infowindow clicklistener
        infoWindowListener();

        // Show the first trail's markers, set it as actionBar's title and zoom in
        setTitle(trailSelected.getName());
        trailSelected.artworkMarkersVisibility(true);
        mMap.setOnMarkerClickListener(this);
        trailSelected.showTrail(TrailsActivity.this);

    }

    // -- BUTTONS --

    // Depending on the menuItem, do an action then close drawer
    // If we return false, no item will be selected even if the action was triggered
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if(menuItem.getItemId()!=trails.size()+1) {
            if (trailSelected != trails.get(menuItem.getItemId())) {
                // Hide markers from previous trail
                isPolylineForTrail = true;
                trailSelected.artworkMarkersVisibility(false);
                if (isCurrentLocSet) {
                    currentLocationMarker.setVisible(false);
                    locationPolyline.setVisible(false);
                }
                //isPolylineForTrail = true;
                if (trailPolyline != null) trailPolyline.setVisible(false);
                trailSelected = trails.get(menuItem.getItemId());
                trailSelected.artworkMarkersVisibility(true);
                trailSelected.zoomIn();
                trailSelected.showTrail(TrailsActivity.this);
                //  isPolylineForTrail = false;
            }

            setTitle(trailSelected.getName());
            drawer.closeDrawer(GravityCompat.START);
        }
        else{

            Intent info = new Intent(TrailsActivity.this, ListPage.class);
            startActivity(info);
        }
        return true;
    }

    // TODO: 16/02/2020 Add more functions (Go back from pressing info window for example)
    @Override
    public void onBackPressed() {
        // Goes back when the drawer is open, else closes app
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // When the current location button is selected, show/hide current location
    // if the current location marker was never created, it calls GetLastLocation which asks for
    // permission if needed
    // if the current location marker is already created, then the visibility is adjusted along with
    // zoom in features.
    private void showDisableCurrentLocation() {
        //hide any open infowindows
        for (Map.Entry element : trailSelected.getArtworkMap().entrySet()) {
            Marker key = (Marker) element.getKey();
            key.hideInfoWindow();
        }

        if (!isCurrentLocSet) {
            startService();
        } else{
            Log.d("mylogr",  "current_loc_is_null");
            //askingForDirection = false;
            isCurrentLocSet = false;
            stopService();
            isCurrentLocSet = false;
            currentLocationMarker.remove();
            currentLocationMarker = null;
            locationPolyline.setVisible(false);
            locationPolyline.remove();
            locationPolyline = null;
            // locationPolyline = null;
            trailSelected.zoomIn();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (trailSelected.getArtworkMap().containsKey(marker)) {
            //moves map to show infowindow (don't know how it works->copy-paste)
            int zoom = (int)mMap.getCameraPosition().zoom;
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new
                    LatLng(marker.getPosition().latitude + (double)90/Math.pow(2, zoom),
                    marker.getPosition().longitude), zoom);
            mMap.animateCamera(cu,480,null);
            marker.showInfoWindow();

            return true;
        } else if (marker.equals(currentLocationMarker)) {
            //do nothing , dont show infowindow as there will be problems
            return true;
        } else return true;
    }

    // TODO: 18/02/2020 Can we use EventBus to transfer these objects from here to InfoPage?
    //infowindow click listener
    private void infoWindowListener() {
        mMap.setOnInfoWindowClickListener(marker -> {
            // Cache the artwork
            EventBus.getDefault()
                    .postSticky(new ArtworkEvent(trailSelected.getArtworkMap().get(marker)));

            Intent info = new Intent(TrailsActivity.this, InfoPage.class);
            startActivity(info);
        });
    }

    // -- FUNCTIONALITIES --

    // Create location button
    public void createButtons() {
        currentLocationButton = findViewById(R.id.currentLocation);
        currentLocationButton.setOnClickListener(v -> showDisableCurrentLocation());
    }

    // Called when a TrailAcquiredEvent has been posted
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(TrailAcquiredEvent event) {
        //EventBus.getDefault().removeStickyEvent(event);
        trails = event.trails;

        trailSelected = trails.get(0);

        // Setting up the map
        // Must be called here so that we can guarantee trails isn't null
        Log.d("eventbus22", "check");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(TrailsActivity.this);
    }

    //start tracking
    void startService(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code);
            return;
        }
        LocationBroadcastReceiver receiver = new LocationBroadcastReceiver();
        IntentFilter filter = new IntentFilter("ACT_LOC");
        registerReceiver(receiver, filter);
        intent = new Intent(TrailsActivity.this, LocationService.class);
        startService(intent);

    }

    //stop tracking
    void stopService(){
        stopService(intent);
    }

    private void setCurrentLocationMarker(LatLng latLng) {
        isCurrentLocSet = true;
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        currentLocationMarker.setVisible(true);
        askingForDirection = true;
    }

    // Result on whether user accepted permission or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_Code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                }
                break;
        }
    }

    //when url comes
    @Override
    public void onTaskDone(Object... values) {


        PolylineOptions polylineOptions = (PolylineOptions) values[0];
        List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(20));

        if (isPolylineForTrail){
            trailPolyline = mMap.addPolyline(polylineOptions);
            trailPolyline.setColor(Color.BLUE);
            trailPolyline.setPattern(pattern);
            isPolylineForTrail = false;
        }

        else{
            Log.d("mylogr",  "check");
            if (locationPolyline!=null){
                locationPolyline.setPoints(polylineOptions.getPoints());
                locationPolyline.setVisible(true);
            }
            else {
                locationPolyline = mMap.addPolyline(polylineOptions);
                locationPolyline.setColor(Color.RED);
                locationPolyline.setPattern(pattern);
            }

            if(!isCurrentLocSet){locationPolyline.setVisible(false);}
            //askingForDirection = false;

        }

        //if polyline is for direction from current location to the trail
      /*  if (askingForDirection) {

            Log.d("mylogr",  "check");
            if (locationPolyline!=null){
                locationPolyline.setPoints(polylineOptions.getPoints());
                locationPolyline.setVisible(true);
            }
            else {
                locationPolyline = mMap.addPolyline(polylineOptions);
                locationPolyline.setColor(Color.RED);
                locationPolyline.setPattern(pattern);
            }

            if(!isCurrentLocSet){locationPolyline.setVisible(false);}
            //askingForDirection = false;
        }
        //if polyline is for trail
        else {
            //Log.d("mylogr",  "why");

            trailPolyline = mMap.addPolyline(polylineOptions);
            trailPolyline.setColor(Color.BLUE);
            trailPolyline.setPattern(pattern);

        }*/
    }

    //inner class (describes what to do when tracking starts)
    public class LocationBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int number = intent.getIntExtra("number", 0);
            if(intent.getAction().equals("ACT_LOC") && number!=counter){

                counter=number;
                double latitude = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                LatLng latLng = new LatLng(latitude, longitude);

                if(currentLocationMarker!=null&&locationPolyline!=null){
                    currentLocationMarker.remove();
                    // locationPolyline.setVisible(false);
                    //locationPolyline.remove();
                    setCurrentLocationMarker(latLng);
                    trailSelected.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());

                }
                else{
                    setCurrentLocationMarker(latLng);
                    trailSelected.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());
                    trailSelected.zoomFit(currentLocationMarker);

                }
            }
        }
    }
}


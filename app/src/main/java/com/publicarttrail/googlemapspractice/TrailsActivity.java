package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.publicarttrail.googlemapspractice.directionhelpers.LocationService;
import com.publicarttrail.googlemapspractice.directionhelpers.TaskLoadedCallback;
import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.events.TrailAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;
import com.publicarttrail.googlemapspractice.pojo.TrailArtwork;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class TrailsActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {
    private GoogleMap mMap;

    // Navigation menu attributes
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private static final int Request_Code = 101;
    private Button currentLocationButton;
    private Marker currentLocationMarker = null;
    private LatLng currentLocLatLng = null;
    private Boolean isCurrentLocSet;
    private Polyline trailPolyline;
    private Polyline locationPolyline;
    private Intent intent;
    private int counter = -1;
    private Boolean isPolylineForTrail = true;
    private Boolean shouldShowLoc = true;

    // Event-related fields
    private final int EVENT_LIMIT = 2;
    private int eventCounter;
    private List<Trail> trails = new ArrayList<>();
    private List<Artwork> artworks = new ArrayList<>();

    private Marker openInfoWindowMarker = null;

    public BiMap<Marker, Artwork> markerArtwork = HashBiMap.create(); // Two-way hashtable
    private LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder(); // Builds a boundary based on the set of LatLngs provided
    private List<Target> targets = new ArrayList<>(); // Assigns marker icon from Picasso

    private Trail currentTrail;

    public List<Trail> getTrails(){
        return trails;
    }

    public LatLng getCurrentLoc(){
        return currentLocLatLng;
    }

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
        //context = getApplicationContext();
        // Setting up the drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setting up the hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // This accounts for rotation, opening app again, etc.
        if (savedInstanceState == null)
            navigationView.setCheckedItem(0); // Set first trail

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
        Log.d("debugon", "startcalled");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("debugon", "resumecalled");
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
        Log.d("debugon", "pausecalled");
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

    // Set the marker icons and populate the menu
    public void setMarkersAndListeners(GoogleMap map, Menu trailsMenu) {
        for (int i = 0; i < artworks.size(); i++) {
            int finalI = i;
            targets.add(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Artwork a = artworks.get(finalI);

                    Marker m = map.addMarker(new MarkerOptions()
                            .position(a.getLatLng())
                            .title(a.getName())
                            .snippet(a.getCreator())
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                    markerArtwork.put(m, a);
                    //latLngBuilder.include(m.getPosition());

                    // Once all icons are set...
                    if (markerArtwork.size() == artworks.size()) {
                        Log.d("bound:3", "onmapready");

                        // Set map, add menu item for each trail
                        for (Trail t : trails) {
                            t.setMap(map);
                            trailsMenu.add(Menu.NONE, t.getId(), Menu.NONE, t.getName());
                            trailsMenu.getItem(t.getId()-1).setIcon(R.drawable.map);
                        }

                        //custom infowindow set up (check newly created class)
                        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(TrailsActivity.this, markerArtwork);

                        //infowindows in this map will use format set in CustomInfoWindowAdapter
                        map.setInfoWindowAdapter(adapter);

                        //set infowindow clicklistener
                        infoWindowListener();

                        // Show all markers
                        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                // TODO: 27/04/2020 Possibly use the default marker?
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

            Picasso.get().load(getIconURL("red", "")).resize(50, 0).into(targets.get(i));
        }
    }

    // When the map is ready, add markers for all trails, sets current location, and creates a listener
    // for any marker selection
    // TODO: 27/04/2020 Possibly save the default marker in resources and just load numbers from URL?
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("supportlogr", "onmapready");

        mMap = googleMap;
        Menu trailsMenu = navigationView.getMenu().getItem(3).getSubMenu();
        setMarkersAndListeners(mMap, trailsMenu);

        mMap.setOnMarkerClickListener(this);
    }

    void zoom() {
        Log.d("bound:3", "check");
        if (currentTrail == null) zoomHome();
        else currentTrail.zoomIn();
    }

    // -- BUTTONS --

    // Depending on the menuItem, do an action then close drawer
    // If we return false, no item will be selected even if the action was triggered
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home: // View all artworks on map
                if (currentTrail != null) {
                    // Hide current trail's markers and polyline, and location
                    showMarkers(currentTrail, false);

                    if (isCurrentLocSet) { // Disable location when selecting new trail
                        stopService();
                        currentLocationMarker.setVisible(false);
                        currentLocationMarker.remove();
                        currentLocationMarker = null;
                        currentLocLatLng = null;
                        if(currentTrail!=null) locationPolyline.setVisible(false);
                    }
                    if (trailPolyline != null) trailPolyline.setVisible(false);

                    // Show all artwork markers
                    currentTrail = null;
                    showMarkers(true);
                    Log.d("bound:3", "navigation");
                    zoomHome();


                    setTitle(R.string.nav_home);
                }
                break;

            case R.id.nav_artworks: // View all artworks via list
                Intent info = new Intent(TrailsActivity.this, ListPage.class);
                startActivity(info);
                break;
            case R.id.nav_about:
                Intent info1 = new Intent(TrailsActivity.this, AboutActivity.class);
                startActivity(info1);
                break;
            default: // Switch trails
                if (!Objects.equals(currentTrail, trails.get(menuItem.getItemId() - 1))) {
                    // Hide current trail's markers and polyline, and location
                    isPolylineForTrail = true;

                    showMarkers(false);
                    if (isCurrentLocSet) { // Disable location when selecting new trail
                        stopService();
                        currentLocationMarker.setVisible(false);
                        currentLocationMarker.remove();
                        currentLocationMarker = null;
                        currentLocLatLng = null;
                        if(currentTrail!=null) locationPolyline.setVisible(false);
                    }
                    if (trailPolyline != null) trailPolyline.setVisible(false);

                    // Show selected trail's markers and polyline
                    currentTrail = trails.get(menuItem.getItemId() - 1);
                    Log.d("mylogrtrail", currentTrail.getName());

                    showMarkers(currentTrail, true);
                    currentTrail.showTrail(TrailsActivity.this);
                    currentTrail.zoomIn();

                    setTitle(currentTrail.getName());
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TODO: 16/02/2020 Add more functions
    @Override
    public void onBackPressed() {
        // Goes back when the drawer is open, else closes app
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else if (openInfoWindowMarker != null && openInfoWindowMarker.isInfoWindowShown())
            openInfoWindowMarker.hideInfoWindow();
        else if (currentTrail != null)
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
        else super.onBackPressed();
    }

    // When the current location button is selected, show/hide current location
    // if the current location marker was never created, it calls GetLastLocation which asks for
    // permission if needed
    // if the current location marker is already created, then the visibility is adjusted along with
    // zoom in features.
    private void showDisableCurrentLocation() {
        //hide any open infowindows
        shouldShowLoc = true;
        if (currentTrail != null) {
            for (TrailArtwork ta : currentTrail.getTrailArtworks()) {
                Marker key = markerArtwork.inverse().get(ta.getArtwork());
                Objects.requireNonNull(key).hideInfoWindow();
            }
        }

        if (!isCurrentLocSet) startService();
        else {
            Log.d("mylogr", "current_loc_is_null");
            stopService();
            currentLocationMarker.setVisible(false);
            currentLocationMarker.remove();
            currentLocationMarker = null;
            currentLocLatLng = null;
            Log.d("mylogr", "marker removed");

            if (currentTrail != null) {
                Log.d("mylogr", "remove polyline");

                locationPolyline.setVisible(false);
                locationPolyline.remove();
                locationPolyline = null;
                currentTrail.locationPath = null;
                currentTrail.zoomIn();
            } else zoomHome();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (markerArtwork.containsKey(marker)) {
            openInfoWindowMarker = marker;

            //moves map to show infowindow (don't know how it works->copy-paste)
            int zoom = (int) mMap.getCameraPosition().zoom;
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new
                    LatLng(marker.getPosition().latitude + (double) 90 / Math.pow(2, zoom),
                    marker.getPosition().longitude), zoom);
            mMap.animateCamera(cu, 480, null);
            marker.showInfoWindow();

            return true;
        } else if (marker.equals(currentLocationMarker)) {
            //do nothing , dont show infowindow as there will be problems
            return true;
        } else return true;
    }

    //infowindow click listener
    private void infoWindowListener() {
        mMap.setOnInfoWindowClickListener(marker -> {
            // Cache the artwork
            EventBus.getDefault()
                    .postSticky(new ArtworkAcquiredEvent(Collections.singletonList(markerArtwork.get(marker))));

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

    public void zoomHome() {
        int padding = 70; // Offset from edges of the map in pixels
        LatLngBounds bounds = latLngBuilder.build();
        Log.d("bound:3", bounds.toString());
        Log.d("bound:3", "hello");
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    public void zoomHomeWithLoc(LatLng currentLocation) {
        int padding = 70; // Offset from edges of the map in pixels
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Artwork artwork:artworks){
            builder.include(artwork.getLatLng());
        }
        builder.include(currentLocation);
        LatLngBounds bounds = builder.build();
        Log.d("bound:3", bounds.toString());
        Log.d("bound:3", "hello");
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    // Called when a TrailAcquiredEvent has been posted
    // TODO: 26/04/2020 Properly set up removing sticky events
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(TrailAcquiredEvent event) {
//      EventBus.getDefault().removeStickyEvent(event);
        trails = event.trails;

        // Setting up the map
        // Must be called here so that we can guarantee trails isn't null
        eventCounter++;
        if (eventCounter == EVENT_LIMIT) {
            Log.d("eventbus22", "check");
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            supportMapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                for (Trail trail : trails)
                    for (Artwork artwork : trail.getArtworks())
                        latLngBuilder.include(artwork.getLatLng());

                int padding = 70; // Offset from edges of the map in pixels
                LatLngBounds bounds = latLngBuilder.build();
                Log.d("bound:3", bounds.toString());
                Log.d("bound:3", "hello");
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

                zoom();
            });
            supportMapFragment.getMapAsync(TrailsActivity.this);
            Log.d("supportlogr", "trailreceive");
        }
    }

    // Called when an ArtworkAcquiredEvent has been posted
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ArtworkAcquiredEvent event) {
        artworks = event.artworks;

        // Setting up the map
        // Must be called here so that we can guarantee trails isn't null
        eventCounter++;
        if (eventCounter == EVENT_LIMIT) {
            Log.d("eventbus22", "check");
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(TrailsActivity.this);
            Log.d("supportlogr", "artreceive");

        }
    }

    // Numbers can go from 1-100 from the prepared things in website
    private String getIconURL(String colour, String character) {
        return "https://raw.githubusercontent.com/Concept211/Google-Maps-Markers/master/images/marker_" + colour + character + ".png";
    }

    // Adjusts visibility of artwork markers in a given trail
    public void showMarkers(Trail t, Boolean show) {
        targets.clear();

        Hashtable<Integer, Artwork> rankArtwork = t.getRankArtwork();
        Log.d("showmarkers", t.getArtworks().get(0).getName());

        for (int i = 1; i <= rankArtwork.size(); i++) {
            Marker m = markerArtwork.inverse().get(rankArtwork.get(i));

            if (show) {
                targets.add(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Objects.requireNonNull(m).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        Objects.requireNonNull(m).setVisible(true);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                Picasso.get().load(getIconURL("red", Integer.toString(i))).resize(50, 0).into(targets.get(i - 1));
            } else Objects.requireNonNull(m).setVisible(false);
        }
    }

    // Adjusts visibility of all artwork markers
    public void showMarkers(Boolean show) {
        targets.clear();
        int i = 0;
        for (Marker m : markerArtwork.keySet()) {
            if (show) {
                targets.add(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Objects.requireNonNull(m).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        Objects.requireNonNull(m).setVisible(true);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                Picasso.get().load(getIconURL("red", "")).resize(50, 0).into(targets.get(i));
                i++;
            } else Objects.requireNonNull(m).setVisible(false);
        }
    }

    //start tracking
    void startService() {
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
    void stopService() {
        isCurrentLocSet = false;
        stopService(intent);
    }

    private void setCurrentLocationMarker(LatLng latLng) {
        if (currentLocationMarker != null) {
            currentLocationMarker.setVisible(false);
            currentLocationMarker.remove();
            currentLocationMarker = null;
            currentLocLatLng = null;
        }

        isCurrentLocSet = true;
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        currentLocLatLng = latLng;
        Log.d("mylogr", "setcurrent2");

        currentLocationMarker.setVisible(true);
    }

    // Result on whether user accepted permission or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

        if (isPolylineForTrail) {
            trailPolyline = mMap.addPolyline(polylineOptions);
            trailPolyline.setColor(Color.BLUE);
            trailPolyline.setPattern(pattern);
            currentTrail.trailPath = polylineOptions;
            isPolylineForTrail = false;
        } else {
            if (isCurrentLocSet) {
                Log.d("mylogr", "check");
                if (locationPolyline != null) {
                    Log.d("mylogr", "check1");

                    locationPolyline.setPoints(polylineOptions.getPoints());
                    locationPolyline.setVisible(true);
                } else {
                    Log.d("mylogr", "check2");
                    locationPolyline = mMap.addPolyline(polylineOptions);
                    locationPolyline.setColor(Color.RED);
                    locationPolyline.setPattern(pattern);
                    currentTrail.locationPath = polylineOptions;
                }
            }
        }
    }

    //inner class (describes what to do when tracking starts)
    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mylogr", "click4");

            int number = intent.getIntExtra("number", 0);
            if (intent.getAction().equals("ACT_LOC") && number != counter && shouldShowLoc) {

                counter = number;
                double latitude = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                LatLng latLng = new LatLng(latitude, longitude);
                Location currentLocation = new Location("");
                currentLocation.setLongitude(longitude);
                currentLocation.setLatitude(latitude);

                if (currentTrail != null && !currentTrail.shouldGetDirections(currentLocation)) {
                    shouldShowLoc = false;
                    stopService();
                    AlertDialog alertDialog = new AlertDialog.Builder(TrailsActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("You are located too far away from the trail for your location to be visible");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> alertDialog.dismiss());
                    alertDialog.show();
                } else {
                    if (currentTrail!=null) {
                        if (currentLocationMarker!=null && locationPolyline!=null) {
                            Log.d("mylogr", "zoomnotcheck2");
                            setCurrentLocationMarker(latLng);
                            currentTrail.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());
                        } else{
                            Log.d("mylogr", "setcurrent");
                            setCurrentLocationMarker(latLng);
                            Log.d("mylogr", "zoomnotcheck");
                            currentTrail.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());
                            currentTrail.zoomFit(currentLocationMarker);
                        }
                    } else {
                        if (currentLocationMarker!=null) {
                            Log.d("mylogr", "zoomnotcheck2");
                            setCurrentLocationMarker(latLng);
                        } else {
                            Log.d("mylogr", "setcurrent");
                            setCurrentLocationMarker(latLng);
                            Log.d("mylogr", "zoomnotcheck");
                            Log.d("mylogr", "zoomnot");
                            zoomHomeWithLoc(currentLocationMarker.getPosition());
                        }
                    }

                }
            }
        }
    }
}

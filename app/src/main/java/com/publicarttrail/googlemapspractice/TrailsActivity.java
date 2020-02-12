package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.publicarttrail.googlemapspractice.directionhelpers.LocationService;
import com.publicarttrail.googlemapspractice.directionhelpers.TaskLoadedCallback;
import com.publicarttrail.googlemapspractice.networking.RetrofitService;
import com.publicarttrail.googlemapspractice.networking.TrailsClient;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailsActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {
    private GoogleMap mMap;

    // Navigation menu attributes
    private DrawerLayout drawer;
    private NavigationView navigationView;

    // Location attributes
    Location mlocation;
    private static final int Request_Code = 101;
    private Button currentLocationButton;
    private Marker currentLocationMarker;
    private Boolean isCurrentLocSet;
    private Polyline trailPolyline;
    private Polyline locationPolyline;
    private Boolean askingForDirection = false;
    private Intent intent;

    // Selecting trails attributes
    // TODO: 11/02/2020 Consider making static in RetrofitService so as to only do GET once
    private List<Trail> trails = new ArrayList<>();
    // TODO: 09/02/2020 Possibility to replace this with id from Trail???
    private Trail trailSelected;

    TrailsClient trailsClient = RetrofitService
            .getRetrofit()
            .create(TrailsClient.class);

    // TODO: 12/02/2020 Consider putting this in MainActivity or RetrofitService
    private Callback<List<Trail>> trailsCallback = new Callback<List<Trail>>() {
        @Override
        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
            trails = response.body();

            // Setting up menu of drawer
            Menu menu = navigationView.getMenu();
            for (Trail t : trails) {
                menu.add(R.id.nav_trails_group, (int) t.getId(), Menu.NONE, t.getName());
            }

            // Setting up the map
            // Must be called here so that we can guarantee trails isn't null
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(TrailsActivity.this);
        }

        @Override
        public void onFailure(Call<List<Trail>> call, Throwable t) {
            t.printStackTrace();
        }
    };

// a new attribute was created - isCurrentLocSet - which basically states whether or not current
// location marker is created. It makes it easier for showDisableCurrentLoc function logic.  It is
// initially set to false in onCreate, and is set to true when user selects the button to show
// current location and has agreed to enable access to permission. This is done in getLastLocation()

    // Starts off the map Activity and relevant location stuff, also creates the buttons and textview
    // needed which are initially set to be invisible
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trails);

        // Give BaseTrails and BaseArtworks a list of their respective objects through GET request
        trailsClient.getTrails()
                .clone()
                .enqueue(trailsCallback);

        // Setting up the toolbar we created as the actionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        if (savedInstanceState == null) {
            // Set first trail (Royal Fort Gardens) as selected item
            navigationView.setCheckedItem(0);
        }

        // Setting up location
        isCurrentLocSet = false;

        // Create the buttons
        createButtons();
    }

    // When the map is ready, add markers for all trails, sets current location, and creates a listener
    // for any marker selection
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set map for all trails
        for (Trail t : trails) {
            t.setMap(mMap);

            for (Artwork a : t.getArtworks()) {
                t.addMarker(a, TrailsActivity.this);
            }
        }

        //custom infowindow set up (check newly created class)
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(TrailsActivity.this, trails);

        //infowindows in this map will use format set in CustomInfoWindowAdapter
        mMap.setInfoWindowAdapter(adapter);

        // Show the first trail's markers, set it as actionBar's title and zoom in
        trailSelected = trails.get(0);
        setTitle(trailSelected.getName());
        trailSelected.artworkMarkersVisibility(true);
        trailSelected.zoomIn();
        mMap.setOnMarkerClickListener(this);
        trailSelected.showTrail(TrailsActivity.this);
    }

    // -- BUTTONS --

    // TODO: Make this better
    // TODO: 09/02/2020 Jonquil needs to understand what code beyond hiding markers do
    // Depending on the menuItem, do an action then close drawer
    // If we return false, no item will be selected even if the action was triggered
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (trailSelected != trails.get(menuItem.getItemId())) {
            // Hide markers from previous trail
            trailSelected.artworkMarkersVisibility(false);
            if (isCurrentLocSet){
                currentLocationMarker.setVisible(false);
                locationPolyline.setVisible(false);
            }

            if (trailPolyline != null) trailPolyline.setVisible(false);
            trailSelected = trails.get(menuItem.getItemId());
            trailSelected.artworkMarkersVisibility(true);
            trailSelected.zoomIn();
            trailSelected.showTrail(TrailsActivity.this);
        }

        setTitle(trailSelected.getName());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
            isCurrentLocSet = false;
            stopService();
            currentLocationMarker = null;
            locationPolyline.setVisible(false);
            trailSelected.zoomIn();
        }
    }

    // -- FUNCTIONALITIES --

    // Create location button
    public void createButtons() {
        currentLocationButton = findViewById(R.id.currentLocation);
        currentLocationButton.setOnClickListener(v -> showDisableCurrentLocation());
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if(trailSelected.getArtworkMap().containsKey(marker)){
            //moves map to show infowindow (don't know how it works->copy-paste)
            int zoom = (int)mMap.getCameraPosition().zoom;
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new
                    LatLng(marker.getPosition().latitude + (double)90/Math.pow(2, zoom),
                    marker.getPosition().longitude), zoom);
            mMap.animateCamera(cu,480,null);
            marker.showInfoWindow();

            return true;
        }
        else if(marker.equals(currentLocationMarker)){
            //do nothing , dont show infowindow as there will be problems
            return true;
        }
        else return true;
    }

    // -- LOCATION FUNCTIONALITIES --

    //if the user denied in the beginning, the permission will appear again and continue to show current location if accepted the second time
    //map is not created anymore here, as this process is done only when the user clicks on the show-location button. Map is now created in oncreate()
    //if user accepted in either times, show the current loc marker and zoom appropriately, and set iscurrentLocSet to be true because current loc marker is created.
    /*private void GetLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null){
                mlocation = location;
                isCurrentLocSet = true;
                setCurrentLocationMarker();
                trailSelected.zoomFit(currentLocationMarker);

            }
        });
    }*/

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

        //if polyline is for direction from current location to the trail
        if (askingForDirection) {

            locationPolyline = mMap.addPolyline(polylineOptions);
            locationPolyline.setColor(Color.RED);
            locationPolyline.setPattern(pattern);
            askingForDirection = false;
        }
        //if polyline is for trail
        else {

            trailPolyline = mMap.addPolyline(polylineOptions);
            trailPolyline.setColor(Color.BLUE);
            trailPolyline.setPattern(pattern);

        }

    }

//inner class
    public class LocationBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ACT_LOC")){

                double latitude = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                LatLng latLng = new LatLng(latitude, longitude);

                if(currentLocationMarker!=null){
                    currentLocationMarker.remove();
                    setCurrentLocationMarker(latLng);
                }
                else{
                    setCurrentLocationMarker(latLng);
                    trailSelected.zoomFit(currentLocationMarker);
                    trailSelected.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());
                }
            }
        }
    }




}


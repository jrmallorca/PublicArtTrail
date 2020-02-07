package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.publicarttrail.googlemapspractice.directionhelpers.FetchURL;
import com.publicarttrail.googlemapspractice.directionhelpers.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailsActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, TaskLoadedCallback {

    private GoogleMap mMap;

    private DrawerLayout drawer;

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_Code = 101;
    private Button currentLocationButton;
    private Marker currentLocationMarker;
    private Boolean isCurrentLocSet;
    private Polyline trailPolyline;
    private Polyline locationPolyline;
    private Boolean askingForDirection = false;

    TrailsClient trailsClient = RetrofitSingleton
            .getRetrofit()
            .create(TrailsClient.class);

    ArtClient artClient = RetrofitSingleton
            .getRetrofit()
            .create(ArtClient.class);

    private Callback<List<Trailv2>> trailsCallback = new Callback<List<Trailv2>>() {
        @Override
        public void onResponse(Call<List<Trailv2>> call, Response<List<Trailv2>> response) {
            trailsv2 = response.body();
        }

        @Override
        public void onFailure(Call<List<Trailv2>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    Callback<List<ArtWorkv2>> artworksCallback = new Callback<List<ArtWorkv2>>() {
        @Override
        public void onResponse(Call<List<ArtWorkv2>> call, Response<List<ArtWorkv2>> response) {
            artWorksv2 = response.body();
        }

        @Override
        public void onFailure(Call<List<ArtWorkv2>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    List<Trailv2> trailsv2;
    List<ArtWorkv2> artWorksv2;

    private List<Trail> trails;
    private Trail trailSelected;
    //map that contains the marker and corresponding image drawable int
    private Map<Marker,Integer> markerAndImage = new HashMap<>();

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

        // Setting up the toolbar we created as the actionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up the drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setting up the hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // This accounts for rotation, opening app again, etc.
        if (savedInstanceState == null) {
            // TODO: Properly set it so Royal Fort Gardens trail is shown first
            // Set Royal Fort Gardens trail as selected item
            navigationView.setCheckedItem(R.id.nav_royalfortgardens);
        }

        // Setting up location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        isCurrentLocSet = false;

        // Setting up the map
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(TrailsActivity.this);

        // Create the buttons
        createButtons();
    }

    // When the map is ready, add markers for all trails, sets current location, and creates a listener
    // for any marker selection
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setting up mock data
        setArtTrail();

        addToMarkerAndImage();

        //custom infowindow set up (check newly created class)
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(TrailsActivity.this, markerAndImage);

        //infowindows in this map will use format set in CustomInfoWindowAdapter

        mMap.setInfoWindowAdapter(adapter);
        // Show the first trail's markers, set it as actionBar's title and zoom in
        trailSelected = trails.get(0);
        setTitle(trailSelected.name);
        trailSelected.artworkMarkersVisibility(true);
        trailSelected.zoomIn();
        mMap.setOnMarkerClickListener(this);
        trailSelected.showTrail(TrailsActivity.this);

    }

    // -- BUTTONS --

    // TODO: Make this better
    // Depending on the menuItem, do an action then close drawer
    // If we return false, no item will be selected even if the action was triggered
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_royalfortgardens:
                if (trailSelected != trails.get(0)) {
                    // Hide markers from previous trail
                    trailSelected.artworkMarkersVisibility(false);
                    if (isCurrentLocSet){
                        currentLocationMarker.setVisible(false);
                        locationPolyline.setVisible(false);
                    }
                    if (trailPolyline!=null) trailPolyline.setVisible(false);
                    trailSelected = trails.get(0);
                    trailSelected.artworkMarkersVisibility(true);
                    trailSelected.zoomIn();
                    trailSelected.showTrail(TrailsActivity.this);
                    break;

                } else break;

            case R.id.nav_clifton:
                if (trailSelected != trails.get(1)) {
                    // Hide markers from previous trail
                    trailPolyline.setVisible(false);
                    trailSelected.artworkMarkersVisibility(false);
                    if (isCurrentLocSet){
                        currentLocationMarker.setVisible(false);

                    }

                    trailSelected = trails.get(1);
                    if(!trailSelected.markers.isEmpty()) trailSelected.showTrail(TrailsActivity.this);
                    trailSelected.artworkMarkersVisibility(true);
                    trailSelected.zoomIn();
                    break;

                } else break;
        }

        setTitle(trailSelected.name);
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
        for (Map.Entry element : markerAndImage.entrySet()) {
            Marker key = (Marker) element.getKey();
            key.hideInfoWindow();
        }
        if (!isCurrentLocSet) {
            GetLastLocation();

        } else if (isCurrentLocSet) {
            if (currentLocationMarker.isVisible()) {
                currentLocationMarker.setVisible(false);
                locationPolyline.setVisible(false);
                trailSelected.zoomIn();


            } else {
                currentLocationMarker.setVisible(true);
                // TODO: May need fixing so that it more accurately tells the user of the location
                trailSelected.zoomFit(currentLocationMarker);
                askingForDirection = true;
                trailSelected.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());

            }
        }
    }

    // -- FUNCTIONALITIES --

    // Sets up mock data.
    // TODO: Replace with mock data from database
    public void setArtTrail() {
        trails = new ArrayList<>();

        trailsClient.getTrails().clone().enqueue(trailsCallback);
        artClient.getArtworks().clone().enqueue(artworksCallback);

        // Creating trails
        Trail royalFort = new Trail(mMap, "Royal Fort Garden");
        Trail clifton = new Trail(mMap, "Clifton");

        // Setting up zoomInArea for trails
        royalFort.zoomInArea = new LatLng(51.457738, -2.602782);
        clifton.zoomInArea = new LatLng(51.466401, -2.619686);

        royalFort.addMarker(tyndallGate, TrailsActivity.this);
        royalFort.addMarker(followMe, TrailsActivity.this);
        royalFort.addMarker(hollow,TrailsActivity.this);
        royalFort.addMarker(royalFortHouse, TrailsActivity.this);
        royalFort.addMarker(owl, TrailsActivity.this);
        royalFort.addMarker(phybuild, TrailsActivity.this);
        royalFort.addMarker(ivyGate, TrailsActivity.this);
        royalFort.addMarker(lizard, TrailsActivity.this);
        royalFort.addMarker(verticalGarden, TrailsActivity.this);


        trails.add(royalFort);
        trails.add(clifton);
    }

    //update markerimagehashmap
    public void addToMarkerAndImage(){
        for(Trail trail:trails){
            trail.addToMarkerImageHashmap(markerAndImage);
        }
    }


    // Create location button
    public void createButtons() {
        currentLocationButton = findViewById(R.id.currentLocation);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisableCurrentLocation();
            }});
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if(trailSelected.hashmap.containsKey(marker)){
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
    private void GetLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mlocation = location;
                    isCurrentLocSet = true;
                    setCurrentLocationMarker();
                    trailSelected.zoomFit(currentLocationMarker);

                }
            }
        });
    }

    private void setCurrentLocationMarker() {
        LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        currentLocationMarker.setVisible(true);
        askingForDirection = true;
        trailSelected.getDirection(TrailsActivity.this, currentLocationMarker.getPosition());

    }

    // Result on whether user accepted permission or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_Code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetLastLocation();
                }
                break;
        }
    }



    //when url comes
    @Override
    public void onTaskDone(Object... values) {


        PolylineOptions polylineOptions = (PolylineOptions) values[0];
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(20));

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




}


package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class TrailsActivity extends AppCompatActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;

    private DrawerLayout drawer;

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_Code = 101;
    private Button currentLocationButton;
    private Marker currentLocationMarker;
    private Boolean isCurrentLocSet;

    private List<Trail> trails;
    private Trail trailSelected;

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

        // Show the first trail's markers and zoom in
        trailSelected = trails.get(0);
        trailSelected.artworkMarkersVisibility(true);
        trailSelected.zoomIn();
    }

    // -- BUTTONS --

    // TODO: Make this better
    // Depending on the menuItem, do an action then close drawer
    // If we return false, no item will be selected even if the action was triggered
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_royalfortgardens:
                trailSelected = trails.get(0);
                trailSelected.artworkMarkersVisibility(true);
                trailSelected.zoomIn();
                break;

            case R.id.nav_clifton:
                trailSelected = trails.get(1);
                trailSelected.artworkMarkersVisibility(true);
                trailSelected.zoomIn();
                break;
        }

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
        if (!isCurrentLocSet) {
            GetLastLocation();

        } else if (isCurrentLocSet) {
            if (currentLocationMarker.isVisible()) {
                currentLocationMarker.setVisible(false);
                trailSelected.zoomIn();

            } else {
                currentLocationMarker.setVisible(true);
                // TODO: May need fixing so that it more accurately tells the user of the location
                trailSelected.zoomFit(currentLocationMarker);
            }
        }
    }

    // -- FUNCTIONALITIES --

    // Sets up mock data.
    // TODO: Replace with mock data from database
    public void setArtTrail() {
        trails = new ArrayList<>();

        // Creating artworks
        ArtWork tyndallGate = new ArtWork("Tyndall Gate", new LatLng(51.458417, -2.603188));
        ArtWork followMe = new ArtWork("Follow Me", new LatLng(51.457620, -2.602613));
        ArtWork hollow = new ArtWork("Hollow", new LatLng(51.457470, -2.600915));
        ArtWork phybuild = new ArtWork("Physics Building", new LatLng(51.458470, -2.602058));
        ArtWork naturePond = new ArtWork("Nature Pond", new LatLng(51.457088, -2.601920));
        ArtWork ivyGate = new ArtWork("Ivy Gate", new LatLng (51.458456, -2.601424));
        ArtWork lizard = new ArtWork("Metalgnu Lizard", new LatLng(51.458830, -2.600851));
        ArtWork verticalGarden = new ArtWork("Vertical Garden", new LatLng(51.458858, -2.600813));
        ArtWork royalFortHouse = new ArtWork("Royal Fort House", new LatLng(51.458318, -2.603357));
        ArtWork owl = new ArtWork("Metalgnu Owl", new LatLng(51.457987, -2.602257));

        // Creating trails
        Trail royalFort = new Trail(mMap, "Royal Fort Garden");
        Trail clifton = new Trail(mMap, "Clifton");

        // Setting up zoomInArea for trails
        royalFort.zoomInArea = new LatLng(51.457738, -2.602782);
        clifton.zoomInArea = new LatLng(51.466401, -2.619686);

        // Adding markers and artworks into trails
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(tyndallGate.latLng).title("Marker in Tyndall Gate")), tyndallGate);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(followMe.latLng).title("Marker in Follow Me")), followMe);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(hollow.latLng).title("Marker in Hollow")), hollow);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(phybuild.latLng).title("Marker in Physics Building")), phybuild);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(naturePond.latLng).title("Marker in Nature Pond")), naturePond);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(ivyGate.latLng).title("Marker in Ivy Gate")), ivyGate);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(lizard.latLng).title("Marker in Lizard")), lizard);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(verticalGarden.latLng).title("Marker in Vertical Garden")), verticalGarden);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(royalFortHouse.latLng).title("Marker in Royal Fort House")), royalFortHouse);
        royalFort.hashmap.put(mMap.addMarker(new MarkerOptions().position(owl.latLng).title("Marker in Owl")), owl);

        trails.add(royalFort);
        trails.add(clifton);
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
}


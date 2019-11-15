package com.publicarttrail.googlemapspractice;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Setting the toolbar we've created as the actionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    // When back button of phone is pressed, do something
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set map type to Satellite view with road names and landmarks
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Boundaries used to display the royalFortGardens region
        LatLngBounds royalFortGardens = new LatLngBounds(
                new LatLng(51.456613, -2.604770), new LatLng(51.459707, -2.598870));

        // Add markers in royalFortGardens
        // This will need to be like a list of locations so we can just use for loop
        LatLng hollow = new LatLng(51.457422, -2.601022);
        LatLng mirrorMaze = new LatLng(51.457571, -2.602565);
        LatLng livingWall = new LatLng(51.458891, -2.600791);

        mMap.addMarker(new MarkerOptions().position(hollow).title("Marker in Hollow"));
        mMap.addMarker(new MarkerOptions().position(mirrorMaze).title("Marker in Mirror Maze"));
        mMap.addMarker(new MarkerOptions().position(livingWall).title("Marker in Living Wall"));

        // Center camera to royalFortGardens and zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(royalFortGardens.getCenter(), 17));
    }
}

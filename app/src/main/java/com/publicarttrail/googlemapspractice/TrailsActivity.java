package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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

import java.util.Map;

public class TrailsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_Code = 101;
    private GoogleMap mMap;
    private AllTrails artTrail;
    private Trail trailSelected;
    private Button back;
    private Button currentLocationButton;
    private Marker currentLocationMarker;
    private TextView trailName;


    //This is just for an example. Later on, it will be linked with the database?
    public void setArtTrail(){
        artTrail = new AllTrails(mMap, "All Trails");
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
        Trail royalFort = new Trail(mMap, "Royal Fort Garden");
        Trail clifton = new Trail(mMap, "Clifton");
        royalFort.zoomInArea = new LatLng(51.457738, -2.602782);
        clifton.zoomInArea = new LatLng(51.466401, -2.619686);
        artTrail.zoomInArea = new LatLng(51.457956, -2.602631);
        artTrail.trails.add(royalFort);
        artTrail.trails.add(clifton);
        royalFort.artWorks.add(tyndallGate);
        royalFort.artWorks.add(followMe);
        royalFort.artWorks.add(hollow);
        royalFort.artWorks.add(phybuild);
        royalFort.artWorks.add(naturePond);
        royalFort.artWorks.add(ivyGate);
        royalFort.artWorks.add(lizard);
        royalFort.artWorks.add(verticalGarden);
        royalFort.artWorks.add(royalFortHouse);
        royalFort.artWorks.add(owl);

    }




//Starts off the map Activity and relevant location stuff, also creates the buttons and textview
// needed which are initially set to be invisible

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trails);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        GetLastLocation();
        back = (Button) findViewById(R.id.button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        currentLocationButton = (Button) findViewById(R.id.currentLocation);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDisableCurrentLocation();
            }
        });


        trailName = (TextView) findViewById(R.id.nameOfTrail);
        trailName.setText(" ");
        setVisibility(View.INVISIBLE);
    }



//when the back button is selected, the following method is implemented.
// It sets all artwork and currentloc markers invisible and sets all trail markers visible.
// Also sets buttons and trail name invisible
    private void goBack() {
        for(Map.Entry element:trailSelected.hashmap.entrySet()){
            Marker key = (Marker)element.getKey();
            key.setVisible(false);

        }
        for(Map.Entry element:artTrail.hashmap.entrySet()) {
            Marker key = (Marker) element.getKey();
            key.setVisible(true);
        }
        artTrail.zoomIn();
        currentLocationMarker.setVisible(false);
        setVisibility(View.INVISIBLE);

    }


//when the current location button is selected, show/hide current location
    private void showDisableCurrentLocation(){if(currentLocationMarker.isVisible()){
        currentLocationMarker.setVisible(false);
        trailSelected.zoomIn();
    }
    else {currentLocationMarker.setVisible(true);
        trailSelected.calculateMiddlePoint(currentLocationMarker);
    }}


    // When a trail is selected, sets all trail markers invisible, creates markers for selected trail,
    // and sets the buttons and textview visible/
    // when art work/current location marker is selected, info window is shown
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (artTrail.hashmap.containsKey(marker)) {
            for(Map.Entry element:artTrail.hashmap.entrySet()){
                Marker key = (Marker)element.getKey();
                key.setVisible(false);
            }

            trailSelected = artTrail.hashmap.get(marker);
            trailSelected.addMarkers();
            trailSelected.zoomIn();
            setVisibility(View.VISIBLE);
            trailName.setText(trailSelected.name);
            return false;
        } else if (trailSelected.hashmap.containsKey(marker) ||
                marker.equals(currentLocationMarker)) {
            marker.showInfoWindow();
            return true;
        } else return true;
    }



//when the map is ready, add markers for all trails, sets current location, and creates a listener
// for any marker selection
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setArtTrail();

        artTrail.addMarkers();
        artTrail.zoomIn();
        LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        currentLocationMarker.setVisible(false);
        mMap.setOnMarkerClickListener(this);

    }

    private void setVisibility(int visibility){
        back.setVisibility(visibility);
        currentLocationButton.setVisibility(visibility);
        trailName.setVisibility(visibility);
    }





    ///////////////////functions part of the current location process

    private void GetLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access location is missing
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, Request_Code);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mlocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapt);
                    supportMapFragment.getMapAsync(TrailsActivity.this);
                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_Code:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetLastLocation();
                }
                break;
        }
    }
}

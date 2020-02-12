package com.publicarttrail.googlemapspractice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d("mylog",  "STOP");
        Toast.makeText(LocationService.this, "DESTROY", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("mylog", "Lat is" + locationResult.getLastLocation().getLatitude() + " , " +
                        "Long is" + locationResult.getLastLocation().getLongitude());
                Intent intent = new Intent("ACT_LOC");
                intent.putExtra("latitude", locationResult.getLastLocation().getLatitude());
                intent.putExtra("longitude", locationResult.getLastLocation().getLongitude());

                sendBroadcast(intent);
            }
        };
    }


    private void requestLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(1);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocation();
        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }
}

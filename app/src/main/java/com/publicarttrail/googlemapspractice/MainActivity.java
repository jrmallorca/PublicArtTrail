package com.publicarttrail.googlemapspractice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//start page
public class MainActivity extends AppCompatActivity {
    private Button button;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrailsActivity();
            }
        });

    if (ContextCompat.checkSelfPermission(MainActivity.this,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    }
    private void openTrailsActivity(){
        Intent intent = new Intent(this, TrailsActivity.class);
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    openTrailsActivity();
                }
                return;
            }
        }
    }
}

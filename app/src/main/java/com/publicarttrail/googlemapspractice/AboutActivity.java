package com.publicarttrail.googlemapspractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private TextView aboutDescriptionText;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutDescriptionText = findViewById(R.id.aboutDescription);



        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        setTitle("About");

    }
}

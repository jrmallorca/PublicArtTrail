package com.publicarttrail.googlemapspractice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.publicarttrail.googlemapspractice.pojo.Artwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Base64;

public class InfoPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("trail"));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"));
        setSupportActionBar(toolbar);
        getIntentsAndIntegrateWithLayout();
    }


    private void getIntentsAndIntegrateWithLayout(){
        String name = getIntent().getStringExtra("name");
        TextView nametext = (TextView) findViewById(R.id.name);
        nametext.setText(name);


        String artist = getIntent().getStringExtra("artist");
        TextView artisttext = (TextView) findViewById(R.id.artist);
        artisttext.setText(artist);


        String description = getIntent().getStringExtra("description");
        TextView descriptiontext = (TextView) findViewById(R.id.description);
        descriptiontext.setText(description);


        Bitmap bitmap = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("image"),0,getIntent()
                        .getByteArrayExtra("image").length);
        ImageView picture = (ImageView) findViewById(R.id.picture);
        picture.setImageBitmap(bitmap);
    }



}

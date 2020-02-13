package com.publicarttrail.googlemapspractice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String name = getIntent().getStringExtra("name");
        String artist = getIntent().getStringExtra("artist");
        String description = getIntent().getStringExtra("description");
        //String image = getIntent().getStringExtra("image");
        //Bitmap bitmap = getBitmap(image);
        /*Bitmap bitmap = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("image"),0,getIntent()
                        .getByteArrayExtra("image").length);
        //Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("image");*/


        TextView nametext = (TextView) findViewById(R.id.name);
        nametext.setText(name);
        TextView artisttext = (TextView) findViewById(R.id.artist);
        artisttext.setText(artist);
        TextView descriptiontext = (TextView) findViewById(R.id.description);
        descriptiontext.setText(description);
        //ImageView picture = (ImageView) findViewById(R.id.picture);
        //picture.setImageBitmap(bitmap);

    }

    public Bitmap getBitmap(String image) {

            byte[] imgBytes = Base64.getDecoder().decode(image);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

        return bitmap;
    }

}

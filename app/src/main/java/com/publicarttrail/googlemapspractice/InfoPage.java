package com.publicarttrail.googlemapspractice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        TextView nameText = findViewById(R.id.name);
        nameText.setText(name);

        String artist = getIntent().getStringExtra("artist");
        TextView artistText = findViewById(R.id.artist);
        artistText.setText(artist);

        String description = getIntent().getStringExtra("description");
        TextView descriptionText = findViewById(R.id.description);
        descriptionText.setText(description);

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("image"),0,getIntent()
                        .getByteArrayExtra("image").length);
        ImageView picture = findViewById(R.id.picture);
        picture.setImageBitmap(bitmap);
    }



}

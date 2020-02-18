package com.publicarttrail.googlemapspractice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.publicarttrail.googlemapspractice.networking.RetrofitService;
import com.publicarttrail.googlemapspractice.networking.TrailsClient;
import com.publicarttrail.googlemapspractice.pojo.Artwork;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//start page
public class MainActivity extends AppCompatActivity {
    private ImageView logo;
    private Timer timer;

    // Selecting trails attributes
    private List<Trail> trails = new ArrayList<>();

    TrailsClient trailsClient = RetrofitService
            .getRetrofit()
            .create(TrailsClient.class);

    private Callback<List<Trail>> trailsCallback = new Callback<List<Trail>>() {
        @Override
        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
            trails = response.body();
        }

        @Override
        public void onFailure(Call<List<Trail>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trailsClient.getTrails().clone().enqueue(trailsCallback);

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(MainActivity.this, TrailsActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 1000);


        logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.welcome);

        try {
            moveTrails();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //infowindow click listener
    private void moveTrails() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(trails);
        byte[] bytes = bos.toByteArray();

        Intent info = new Intent(MainActivity.this, TrailsActivity.class);
        info.putExtra("trail", bytes);
        startActivity(info);
    }
}

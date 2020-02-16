package com.publicarttrail.googlemapspractice;
import android.content.Intent;
import android.widget.ImageView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

//start page
public class MainActivity extends AppCompatActivity {
    private ImageView logo;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, TrailsActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);


        logo = findViewById(R.id.logo);

        logo.setImageResource(R.drawable.web_hi_res_512);
    }

}

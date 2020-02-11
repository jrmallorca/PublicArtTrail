package com.publicarttrail.googlemapspractice;

import android.content.Intent;
import android.app.Activity;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

//start page
public class MainActivity extends AppCompatActivity {
    //private Button button;
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
        }, 2000);





        /*button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrailsActivity();
            }
        });*/

        logo = (ImageView) findViewById(R.id.logo);

        logo.setImageResource(R.drawable.web_hi_res_512);
    }
   /* private void openTrailsActivity(){
        Intent intent = new Intent(this, TrailsActivity.class);
        startActivity(intent);
    }*/
}

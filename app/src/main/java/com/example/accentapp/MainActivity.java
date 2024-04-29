package com.example.accentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    Timer timer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView2);
        slogan = findViewById(R.id.textView3);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image.setAnimation(topAnim);
        image.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Secondpage.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }


}
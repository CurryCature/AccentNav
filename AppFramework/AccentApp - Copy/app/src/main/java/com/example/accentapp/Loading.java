package com.example.accentapp;

import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Timer;

public class Loading extends AppCompatActivity{

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        image = (ImageView) findViewById(R.id.image);

        Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        image.startAnimation(blink);
    }

}


package com.example.accentapp;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
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
import java.util.TimerTask;

public class Loading extends AppCompatActivity{

    ImageView image;
    TextView text1, text2, text3, text4;
    Timer timer;

    private int port;
    private String hostname;
    private String outputFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        image = (ImageView) findViewById(R.id.image);
        text1 = (TextView) findViewById(R.id.textView);
        text2= (TextView) findViewById(R.id.textView2);
        text3 = (TextView) findViewById(R.id.textView3);
        text4 = (TextView) findViewById(R.id.textView4);

        Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        image.startAnimation(blink);
        Animation flash = AnimationUtils.loadAnimation(this, R.anim.flash);
        text2.startAnimation(flash);
        text3.startAnimation(flash);
        text4.startAnimation(flash);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Loading.this, ThirdPage.class);
                intent.putExtra("outputFilePath", outputFilePath);
                startActivity(intent);
                finish();
            }
        }, 2000);


    }

}


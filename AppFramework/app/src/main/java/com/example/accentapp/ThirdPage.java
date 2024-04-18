package com.example.accentapp;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Timer;

public class ThirdPage extends AppCompatActivity{
    TextView textView;
    ImageView logo;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third_page);

        textView = (TextView) findViewById(R.id.textView2);
        logo = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdPage.this, Secondpage.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
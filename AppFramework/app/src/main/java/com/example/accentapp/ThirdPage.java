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
    Button btn_back;
    Button btn_ex;

    private boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third_page);

        TextView r1 = findViewById(R.id.rank1);
        TextView r2 = findViewById(R.id.rank2);
        TextView r3 = findViewById(R.id.rank3);
        TextView r4 = findViewById(R.id.rank4);
        TextView r5 = findViewById(R.id.rank5);
        TextView p2 = findViewById(R.id.percent2);
        TextView p3 = findViewById(R.id.percent3);
        TextView p4 = findViewById(R.id.percent4);
        TextView p5 = findViewById(R.id.percent5);

        r2.setVisibility(View.GONE);
        r3.setVisibility(View.GONE);
        r4.setVisibility(View.GONE);
        r5.setVisibility(View.GONE);
        p2.setVisibility(View.GONE);
        p3.setVisibility(View.GONE);
        p4.setVisibility(View.GONE);
        p5.setVisibility(View.GONE);

        textView = (TextView) findViewById(R.id.textView2);
        logo = (ImageView) findViewById(R.id.imageView);
        btn_back = (Button) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdPage.this, Secondpage.class);
                startActivity(intent);
                finish();
            }
        });

        btn_ex = (Button) findViewById(R.id.btnex);
        btn_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPressed = !isPressed;
                if (isPressed) {
                    r2.setVisibility(View.VISIBLE);
                    r3.setVisibility(View.VISIBLE);
                    r4.setVisibility(View.VISIBLE);
                    r5.setVisibility(View.VISIBLE);
                    p2.setVisibility(View.VISIBLE);
                    p3.setVisibility(View.VISIBLE);
                    p4.setVisibility(View.VISIBLE);
                    p5.setVisibility(View.VISIBLE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                } else {
                    r2.setVisibility(View.GONE);
                    r3.setVisibility(View.GONE);
                    r4.setVisibility(View.GONE);
                    r5.setVisibility(View.GONE);
                    p2.setVisibility(View.GONE);
                    p3.setVisibility(View.GONE);
                    p4.setVisibility(View.GONE);
                    p5.setVisibility(View.GONE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
                }
            }
        });

    }


}
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

import java.util.List;
import java.util.Timer;


public class ThirdPage extends AppCompatActivity{
    TextView textView;
    ImageView logo;
    Button btn_back;
    Button btn_ex;
    private boolean isPressed = false;
    List<String> keys = Loading.keys;
    List<String> values = Loading.values;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third_page);

        TextView r = findViewById(R.id.res);
        TextView r1 = findViewById(R.id.rank1);
        TextView r2 = findViewById(R.id.rank2);
        TextView r3 = findViewById(R.id.rank3);
        TextView r4 = findViewById(R.id.rank4);
        TextView r5 = findViewById(R.id.rank5);
        TextView p1 = findViewById(R.id.percent1);
        TextView p2 = findViewById(R.id.percent2);
        TextView p3 = findViewById(R.id.percent3);
        TextView p4 = findViewById(R.id.percent4);
        TextView p5 = findViewById(R.id.percent5);
        View menu = findViewById(R.id.menu);


        int numKeys = Math.min(keys.size(), 5); // Get the minimum of 5 and the size of keys list
        for (int i = 0; i < numKeys; i++) {
            String key = keys.get(i);
            key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
            switch (i) {
                case 0:
                    r.setText(key);
                    r1.setText("1. " + key);
                    p1.setText(values.get(i));
                    break;
                case 1:
                    r2.setText("2. " + key);
                    p2.setText(values.get(i));
                    break;
                case 2:
                    r3.setText("3. " + key);
                    p3.setText(values.get(i));
                    break;
                case 3:
                    r4.setText("4. " + key);
                    p4.setText(values.get(i));
                    break;
                case 4:
                    r5.setText("5. " + key);
                    p5.setText(values.get(i));
                    break;
            }
        }




        r1.setVisibility(View.GONE);
        r2.setVisibility(View.GONE);
        r3.setVisibility(View.GONE);
        r4.setVisibility(View.GONE);
        r5.setVisibility(View.GONE);
        p1.setVisibility(View.GONE);
        p2.setVisibility(View.GONE);
        p3.setVisibility(View.GONE);
        p4.setVisibility(View.GONE);
        p5.setVisibility(View.GONE);
        menu.setVisibility(View.GONE);

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
                    r1.setVisibility(View.VISIBLE);
                    p1.setVisibility(View.VISIBLE);
                    if (keys.size() > 1) {
                        r2.setVisibility(View.VISIBLE);
                        p2.setVisibility(View.VISIBLE);
                    }
                    if (keys.size() > 2) {
                        r3.setVisibility(View.VISIBLE);
                        p3.setVisibility(View.VISIBLE);
                    }
                    if (keys.size() > 3) {
                        r4.setVisibility(View.VISIBLE);
                        p4.setVisibility(View.VISIBLE);
                    }
                    if (keys.size() > 4) {
                        r5.setVisibility(View.VISIBLE);
                        p5.setVisibility(View.VISIBLE);
                    }
                    menu.setVisibility(View.VISIBLE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                } else {
                    r1.setVisibility(View.GONE);
                    r2.setVisibility(View.GONE);
                    r3.setVisibility(View.GONE);
                    r4.setVisibility(View.GONE);
                    r5.setVisibility(View.GONE);
                    p1.setVisibility(View.GONE);
                    p2.setVisibility(View.GONE);
                    p3.setVisibility(View.GONE);
                    p4.setVisibility(View.GONE);
                    p5.setVisibility(View.GONE);
                    menu.setVisibility(View.GONE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
                }
            }
        });

    }


}
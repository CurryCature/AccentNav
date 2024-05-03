package com.example.accentapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ThirdPage extends AppCompatActivity {
    TextView textView;
    TextView r;

    Button btn_back;
    Button btn_ex;
    private boolean isPressed = false;
    final List<String> keys = Loading.keys;
    final List<String> values = Loading.values;
    final List<TextView> rankTexts = new ArrayList<>();
    final List<TextView> percentTexts = new ArrayList<>();
    View menu;
    int numKeys = 0;
    String title = "Error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third_page);

        // Initializing Views
        r = findViewById(R.id.res);
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
        menu = findViewById(R.id.menu);

        // Adding TextViews to List for easy access
        rankTexts.add(r1);
        rankTexts.add(r2);
        rankTexts.add(r3);
        rankTexts.add(r4);
        rankTexts.add(r5);

        percentTexts.add(p1);
        percentTexts.add(p2);
        percentTexts.add(p3);
        percentTexts.add(p4);
        percentTexts.add(p5);

        // Set default visibility of TextViews
        menu.setVisibility(View.GONE);
        if (!keys.isEmpty()) {
            setTextViewsVisibility(0);

            title = keys.get(0);
            title = title.substring(0, 1).toUpperCase() + title.substring(1).toLowerCase();
            r.setText(title);

            // Set text and visibility of TextViews
            numKeys = Math.min(keys.size(), 5);
            for (int i = 0; i < numKeys; i++) {
                String key = keys.get(i);
                key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
                rankTexts.get(i).setText((i + 1) + ". " + key);
                percentTexts.get(i).setText(values.get(i));
            }
        } else {
            // If keys list is empty, hide all TextViews
            setTextViewsVisibility(0);
        }

        textView = findViewById(R.id.textView2);
        btn_back = findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThirdPage.this, Secondpage.class);
                startActivity(intent);
                finish();
            }
        });

        btn_ex = findViewById(R.id.btnex);
        btn_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPressed = !isPressed;
                if (isPressed) {
                    setTextViewsVisibility(Math.max(numKeys, 1));
                    menu.setVisibility(View.VISIBLE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                } else {
                    setTextViewsVisibility(0);
                    menu.setVisibility(View.GONE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPressed", isPressed);
        outState.putStringArrayList("keys", new ArrayList<>(keys));
        outState.putStringArrayList("values", new ArrayList<>(values));
        outState.putInt("numKeys", Math.min(keys.size(), 5));
        outState.putString("title", title);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isPressed = savedInstanceState.getBoolean("isPressed");
        keys.clear();
        keys.addAll(savedInstanceState.getStringArrayList("keys"));
        values.clear();
        values.addAll(savedInstanceState.getStringArrayList("values"));
        title = savedInstanceState.getString("title");
        r.setText(title);
        numKeys = savedInstanceState.getInt("numKeys");
        menu.setVisibility(View.VISIBLE);
        btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
        setTextViewsVisibility(Math.max(numKeys, 1));
        for (int i = 0; i < numKeys; i++) {
            String key = keys.get(i);
            key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
            rankTexts.get(i).setText((i + 1) + ". " + key);
            percentTexts.get(i).setText(values.get(i));
        }
    }


    private void setTextViewsVisibility(int numKeys) {
        for (int i = 0; i < 5; i++) {
            if (i < numKeys) {
                rankTexts.get(i).setVisibility(View.VISIBLE);
                percentTexts.get(i).setVisibility(View.VISIBLE);
            } else {
                rankTexts.get(i).setVisibility(View.GONE);
                percentTexts.get(i).setVisibility(View.GONE);
            }
        }
    }

}

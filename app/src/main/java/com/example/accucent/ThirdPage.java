package com.example.accucent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                keys.clear();
                values.clear();
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

        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPressed) {
                    isPressed = true;
                    setTextViewsVisibility(Math.max(numKeys, 1));
                    menu.setVisibility(View.VISIBLE);
                    btn_ex.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap screenshot = takeScreenshot(getWindow().getDecorView().getRootView());
                        // Save screenshot
                        File imagePath = saveScreenshot(screenshot);
                        // Share screenshot
                        shareScreenshot(imagePath, ThirdPage.this);
                    }
                }, 200); // Delay for 200 milliseconds
            }
        });
    }

    public Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public File saveScreenshot(Bitmap bitmap) {
        File imagePath = new File(getExternalFilesDir(null) + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("TAG", e.getMessage(), e);
        }
        return imagePath;
    }

    public void shareScreenshot(File file, Context context) {
        Uri photoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Share Screenshot"));
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
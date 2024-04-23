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
import android.widget.TextView;

public class ThirdPage extends AppCompatActivity{
    TextView textView;
    ImageView logo;
    Button button;

    public TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        // Get the Intent that started this activity
        Intent intent = getIntent();
        // Get the server response from the Intent
        String serverResponse = intent.getStringExtra("serverResponse");

        // Find the TextView in which to display the server response
        TextView responseTextView = findViewById(R.id.responseTextView);
        // Set the server response as the text of the TextView
        responseTextView.setText(serverResponse);

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

        // Get the outputFilePath, port, and hostname from the Intent
        String outputFilePath = intent.getStringExtra("outputFilePath");
        int port = intent.getIntExtra("port", 0); // 0 is a default value
        String hostname = intent.getStringExtra("hostname");
        //Create an instance of AppRunnable
        AppRunnable appRunnable = new AppRunnable(8080,     "localhost", outputFilePath);
        //Start the thread
        Thread thread = new Thread(appRunnable);
        thread.start();
        responseTextView.setText("Server response: " + appRunnable.serverAnswer.toString());

    }

}
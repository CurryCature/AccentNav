package com.example.accentapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Secondpage extends AppCompatActivity {

    private static final String TAG = "recording";
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private MediaRecorder mediaRecorder;
    private String outputFilePath;

    private long fileSizeInBytes = 0;
    private String hostname = "130.229.141.0"; // The server's IP address or hostname
    private int port = 28561; // The server's port

    private byte[] userInputBytes = new byte[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_secondpage);
        checkPermissions();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the navigation bar (optional)
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );*/

        // Initialize outputFile(the string output file path)
        outputFilePath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recording.aac").getAbsolutePath();


        // Upload button
        Button btn1 = findViewById(R.id.btn_up);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
            }
        });

        // Recording button
        Button btnRecord = findViewById(R.id.btn_mic);
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (checkPermissions()) {
                            v.setPressed(true);
                            startRecording();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        stopRecording();
                        Intent intent = new Intent(Secondpage.this, Loading.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true; // Return true to consume the event
            }
        });

    }
    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(outputFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Recording preparation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Recording preparation failed", e);
            if (mediaRecorder != null) {
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }


    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
        //Extracting the audio file.
        File audioFile = new File(outputFilePath);
        if (audioFile.exists()) {
            // The file exists
            fileSizeInBytes = audioFile.length();
        } else {
            // The file does not exist
            fileSizeInBytes = 0;
        }
        try {
            userInputBytes = Files.readAllBytes(Paths.get(outputFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Create an instance of TCPClient
        com.example.accentapp.tcpclient.TCPClient tcpClient = new com.example.accentapp.tcpclient.TCPClient();
        try {
            // Call the askServer method
            byte[] serverResponse = tcpClient.askServer(hostname, port, userInputBytes);
            // Handle the server response here
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean checkPermissions() {
        boolean permissionGranted = true;

        // Check RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
            permissionGranted = false;
        }

        // Check WRITE_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            permissionGranted = false;
        }

        return permissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION_REQUEST_CODE:
                // Check if RECORD_AUDIO permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start recording
                    startRecording();
                } else {
                    // Permission denied, show a message or handle it accordingly
                    Toast.makeText(this, "RECORD_AUDIO Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                // Check if WRITE_EXTERNAL_STORAGE permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, do nothing
                } else {
                    // Permission denied, show a message or handle it accordingly
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording(); // Stop recording when the activity is destroyed
    }
}
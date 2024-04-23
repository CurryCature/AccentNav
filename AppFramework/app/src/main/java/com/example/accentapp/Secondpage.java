package com.example.accentapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;


public class Secondpage extends AppCompatActivity{

    private static final String TAG = "recording";
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private static final int READ_MEDIA_AUDIO_PERMISSION_REQUEST_CODE = 1001;
    static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final int MIN_RECORDING_DURATION = 5000; // 5 seconds
    private static final int MAX_RECORDING_DURATION = 60000; // 60 seconds
    private MediaRecorder mediaRecorder;
    public String outputFilePath;
    private long fileSizeInBytes = 0;
    private String hostname = "130.229.141.0"; // The server's IP address or hostname
    private int port = 28561; // The server's port
    private byte[] userInputBytes = new byte[0];
    private ActivityResultLauncher<Intent> requestPermissionLauncher;

    //Control duration
    private Handler handler;
    private boolean isRecording = false;
    private long startTimeMillis;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_secondpage);
        checkPermissions();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize outputFile(the string output file path)
        outputFilePath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recording.mp4").getAbsolutePath();

        handler = new Handler();

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
                        if(stopRecording()) {
                            Intent intent = new Intent(Secondpage.this, Loading.class);
                            intent.putExtra("outputFilePath", outputFilePath);
                            intent.putExtra("port", port);
                            intent.putExtra("hostname", hostname);
                            startActivity(intent);
                            finish();
                        }
                        break;
                }
                return true; // Return true to consume the event
            }
        });

    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(outputFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            startTimeMillis = System.currentTimeMillis(); // Record start time
            scheduleStopRecording(MAX_RECORDING_DURATION);
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

    private boolean stopRecording() {
        boolean continueNext = true;
        if (mediaRecorder != null && isRecording) {
            long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
            if (elapsedTimeMillis >= MIN_RECORDING_DURATION) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                handler.removeCallbacksAndMessages(null); // Cancel scheduled stop
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
            } else {
                continueNext = false;
                handler.removeCallbacksAndMessages(null);
                mediaRecorder.stop();
                isRecording = false;
                Toast.makeText(this, "Minimum recording duration not reached", Toast.LENGTH_SHORT).show();
            }
        }
        return continueNext;
    }



    // stop recording when exceed max length
    private void scheduleStopRecording(long durationMillis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    stopRecording();
                    Toast.makeText(Secondpage.this, "Maximum recording duration reached", Toast.LENGTH_SHORT).show();
                }
            }
        }, durationMillis);
    }


    private boolean checkPermissions() {
        String[] permissions = null;

        // Check for RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, and READ_MEDIA_AUDIO permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_AUDIO // Assuming READ_MEDIA_AUDIO for audio access
            };
        } else {
            // Handle pre-Android 13 permission
            permissions = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        // Request permissions if not already granted
        if (permissions != null) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permissions[0]);
            for (int i = 0; i < permissions.length; i++) {
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
                    return false; // Permissions not granted, wait for onRequestPermissionsResult
                }
                permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            }
        }

        // All permissions granted
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean recordAudioGranted = false;
            boolean writeStorageGranted = false;
            boolean readMediaGranted = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                    recordAudioGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    writeStorageGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permissions[i].equals(Manifest.permission.READ_MEDIA_AUDIO)) {
                    readMediaGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            // Handle permission grant/denial logic based on individual permissions
            if (recordAudioGranted && (writeStorageGranted || readMediaGranted)) {
                // All permissions granted, proceed with recording and saving audio
                // ... (your recording and storage logic)
            } else {
                // Handle permission denial for any permission
                showPermissionDeniedMessageAndRequestPermissionAgain();
            }
        }
    }


    private void showPermissionDeniedMessageAndRequestPermissionAgain() {
        Toast.makeText(this, "Permission denied. Please grant the required permission to continue.", Toast.LENGTH_SHORT).show();
        // Provide an option for the user to manually grant the permission again
        // For example, you can open the app settings page
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording(); // Stop recording when the activity is destroyed
    }
}
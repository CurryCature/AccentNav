package com.example.accentapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Secondpage extends AppCompatActivity {

    private static final String TAG = "recording";
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private static final int READ_MEDIA_AUDIO_PERMISSION_REQUEST_CODE = 1001;
    static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final int MIN_RECORDING_DURATION = 5000; // 5 seconds
    private static final int MAX_RECORDING_DURATION = 60000; // 60 seconds
    private static final int REQUEST_PICK_AUDIO = 1002;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;
    private Uri audioUri;
    private TextView timerTextView;
    private Handler handler;
    private boolean nxtPage = false;
    private long startTimeMillis;
    private String lastSentence = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_secondpage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkPermissions();
        handler = new Handler();

        // Example text display
        TextView TextDisplay = findViewById(R.id.text_sentence);

        String[] sentences = {"'Swedish cuisine is known for its diverse range of dishes that reflect the country's rich culinary heritage and use of locally sourced ingredients.  Whether it's enjoying a coffee break with a cinnamon bun, cheeses, and salads, Swedish cuisine provides a delicious exploration of the country's culture and history through food.'",
                "'The Mediterranean Sea, bordered by Europe, Africa, and Asia, is renowned for its clear blue waters and its significant role in shaping the cultures and cuisines of the diverse regions that surround it. The vast landscapes and mesmerizing nature allows tourists to enjoy lovely holidays every year.'",
                "'Planning a birthday party requires meticulous attention to detail, from selecting the perfect venue and coordinating schedules to curating the guest list and organizing entertainment that suits the celebrant's preferences in order to create a joyous and memorable experience for both the birthday honoree and their guests.'",
                "'Visiting a castle transports you back in time, as you wander through grand halls, admire centuries-old tapestries, climb ancient stone staircases, and peer out from towering battlements, immersing yourself in the rich history and majestic beauty of the fortress's storied past.'",
                "'The arrival of spring brings warmer temperatures and longer days, signaling the end of winter. You can hear the birds chirping and admire the beauty of the bloomed colorful flowers, while admiring the modern architecture of the city center.'"};
        Random random = new Random();

        int r;
        do {
            r = random.nextInt(sentences.length);
        } while (sentences[r].equals(lastSentence));

        TextDisplay.setText(sentences[r]);
        lastSentence = sentences[r];

        // Upload button
        Button btnUpload = findViewById(R.id.btn_up);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                uploadAudio();
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
                            timerTextView.setText("Recording Time (minimum 5s): 0s");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        stopRecording();
                        int r;
                        do {
                            r = random.nextInt(sentences.length);
                        } while (sentences[r].equals(lastSentence));
                        TextDisplay.setText(sentences[r]);
                        lastSentence = sentences[r];
                        timerTextView.setText("Minimum record duration: 5 seconds");

                        if (nxtPage) {
                            Intent intent = new Intent(Secondpage.this, Loading.class);
                            intent.putExtra("outputFilePath", audioUri.getPath());
                            startActivity(intent);
                            finish();
                        }
                        break;
                }
                return true; // Return true to consume the event
            }
        });
        timerTextView = findViewById(R.id.timerTextView);
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nxtPage) {
                    long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
                    int elapsedSeconds = (int) (elapsedTimeMillis / 1000);
                    timerTextView.setText("Recording Time (minimum 5s): " + elapsedSeconds + "s");
                    startTimer();
                }
            }
        }, 1000); // update every second
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(getOutputFile().getPath());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            startTimeMillis = System.currentTimeMillis(); // Record start time
            scheduleStopRecording(MAX_RECORDING_DURATION);
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            nxtPage = true;
            startTimer();
        } catch (IOException e) {
            Toast.makeText(this, "Recording preparation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Recording preparation failed", e);
            if (mediaRecorder != null) {
                mediaRecorder.release();
                mediaRecorder = null;
            }
            nxtPage = false;
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null && nxtPage) {
            long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
            if (elapsedTimeMillis >= MIN_RECORDING_DURATION) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                handler.removeCallbacksAndMessages(null); // Cancel scheduled stop
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                nxtPage = true;

            } else {
                nxtPage = false;
                handler.removeCallbacksAndMessages(null);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mediaRecorder.stop();
                Toast.makeText(this, "Minimum recording duration not reached", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // stop recording when exceed max length
    private void scheduleStopRecording(long durationMillis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nxtPage) {
                    stopRecording();
                    Toast.makeText(Secondpage.this, "Maximum recording duration reached", Toast.LENGTH_SHORT).show();
                }
            }
        }, durationMillis);
    }

    private void uploadAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_PICK_AUDIO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();
            try {
                // Copy the selected audio file to the app's internal storage
                File copiedFile = copyAudioFileToInternalStorage(audioUri);
                // Check if the copied file is not null and exists
                if (copiedFile != null && copiedFile.exists()) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(copiedFile.getPath());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            int duration = mp.getDuration() / 1000; // in seconds
                            if (duration >= MIN_RECORDING_DURATION / 1000 && duration <= MAX_RECORDING_DURATION / 1000) {
                                // Audio duration is within the allowed range
                                // Proceed with further processing
                                Toast.makeText(Secondpage.this, "Audio duration: " + duration + " seconds", Toast.LENGTH_SHORT).show();
                                // Go to Loading activity and send audio to server
                                Intent intent = new Intent(Secondpage.this, Loading.class);
                                intent.putExtra("outputFilePath", copiedFile.getPath());
                                startActivity(intent);
                            } else {
                                // Audio duration is not within the allowed range
                                Toast.makeText(Secondpage.this, "Audio duration must be between 5 and 60 seconds", Toast.LENGTH_SHORT).show();
                            }
                            mediaPlayer.release();
                        }
                    });
                    mediaPlayer.prepareAsync();
                } else {
                    Toast.makeText(Secondpage.this, "Error copying audio file", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File copyAudioFileToInternalStorage(Uri audioUri) {
        try {
            // Get a reference to the content resolver
            ContentResolver resolver = getContentResolver();
            // Get the file's content type
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = resolver.query(audioUri, projection, null, null, null);
            String fileType = null;
            if (cursor != null && cursor.moveToFirst()) {
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
            if (cursor != null) {
                cursor.close();
            }
            // Create the destination file in the internal storage
            File destFile = new File(getFilesDir(), "audio_file." + fileType);
            // Open an input stream to read from the content resolver
            InputStream inputStream = resolver.openInputStream(audioUri);
            // Open an output stream to write to the internal storage file
            OutputStream outputStream = new FileOutputStream(destFile);
            // Copy the content from the input stream to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            // Close the streams
            outputStream.close();
            inputStream.close();
            // Return the destination file
            return destFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    private File getOutputFile() {
        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File outputFile = new File(outputDir, "ARecording.mp3");
        audioUri = Uri.fromFile(outputFile);
        return outputFile;
    }

    private boolean checkPermissions() {
        String[] permissions = null;

        // Check for RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, and READ_MEDIA_AUDIO permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_MEDIA_AUDIO
            };
        } else {
            // Handle pre-Android 13 permission
            permissions = new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        // Request permissions if not already granted
        int permissionCheck = ContextCompat.checkSelfPermission(this, permissions[0]);
        for (int i = 0; i < permissions.length; i++) {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
                return false; // Permissions not granted, wait for onRequestPermissionsResult
            }
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
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

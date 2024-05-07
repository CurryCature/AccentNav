package com.example.accucent;

import android.content.Intent;
import android.os.Build;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.ByteBuffer;


public class Loading extends AppCompatActivity implements RepositoryCallback<byte[]>{

    ImageView image;
    TextView text1, text2, text3, text4;
    Timer timer;

    //private int port = 28561;
    private int port = 20013;
    //private String hostname = "130.229.154.67";
    private String hostname = "vm.cloud.cbh.kth.se";

    byte[] dataToSend = new byte[0];

    byte[]dataToSendWithoutHeader = new byte[0];

    private long fileSizeInBytes = 0;

    byte[] responseData = new byte[0];

    public static List<String> keys = new ArrayList<>();
    public static List<String> values = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        String outputFilePath = intent.getStringExtra("outputFilePath");
        //String outputFilePath = "/storage/emulated/0/Android/data/com.example.accentapp/files/Music/ARecording.mp3";

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

        //timer = new Timer();
        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Loading.this, ThirdPage.class);
                startActivity(intent);
                finish();
            }
        }, 20000);*/

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
            // Read the audio file
            dataToSendWithoutHeader = Files.readAllBytes(Paths.get(outputFilePath));
            // Convert the length to a byte array
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            byteBuffer.putInt(dataToSendWithoutHeader.length);
            byte[] lengthBytes = byteBuffer.array();
            //create a byte array to store the audio file and the header(file size)
            dataToSend = new byte[lengthBytes.length + dataToSendWithoutHeader.length];
            // Copy the length and the data into the new byte array
            System.arraycopy(lengthBytes, 0, dataToSend, 0, lengthBytes.length);
            System.arraycopy(dataToSendWithoutHeader, 0, dataToSend, lengthBytes.length, dataToSendWithoutHeader.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    askServer(hostname, port, dataToSend, Loading.this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    public void onComplete(byte[] result) {
        keys.clear();
        values.clear();

        if (result != null && result.length > 0) {
            // Convert the byte array to a JSON string
            String jsonData = new String(result);

            if (jsonData.contains("\"error\":")) {
                int start = jsonData.indexOf(":") + 3;
                int end = jsonData.lastIndexOf("\"");
                String errorMessage = jsonData.substring(start, end);
                keys.add("Error");
                values.add(errorMessage);
                return;
            }

            // Split the string by commas and remove curly braces
            String[] keyValuePairs = jsonData.substring(1, jsonData.length() - 1).split(",");

            // Create a Map to store key-value pairs
            Map<String, Double> dataMap = new HashMap<>();
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.trim().split(":");
                dataMap.put(keyValue[0].trim().replace("\"", ""), Double.parseDouble(keyValue[1].trim()));
            }

            // Create a custom class to hold key-value pair
            class KeyValue {
                String key;
                double value;

                public KeyValue(String key, double value) {
                    this.key = key;
                    this.value = value;
                }
            }

            // Convert Map entries to a List of KeyValue objects
            List<KeyValue> keyValueList = new ArrayList<>();
            for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
                keyValueList.add(new KeyValue(entry.getKey(), entry.getValue()));
            }

            // Sort the List by value in descending order
            Collections.sort(keyValueList, new Comparator<KeyValue>() {
                @Override
                public int compare(KeyValue o1, KeyValue o2) {
                    return Double.compare(o2.value, o1.value);
                }
            });

            // Separate keys and values into separate lists

            for (KeyValue keyValue : keyValueList) {
                keys.add(keyValue.key);
                double doubleValue = keyValue.value * 100;
                DecimalFormat df = new DecimalFormat("0.00");
                values.add(df.format(doubleValue) + "%");
            }



            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Loading.this, ThirdPage.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // Handle error here
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  Intent intent = new Intent(Loading.this, ThirdPage.class);
                                  startActivity(intent);
                                  finish();
                              }
                          }
            );
        }
    }

    private static final int BUFFERSIZE = 1024;

    public static byte[] askServer(String hostname, int port, byte[] toServerBytes, RepositoryCallback<byte[]> callback) throws IOException {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // A thread for sending and receiving
        Thread sendAndReceiveThread = new Thread(() -> {
            try (Socket clientSocket = new Socket(hostname, port);
                 OutputStream output = clientSocket.getOutputStream();
                 InputStream input = clientSocket.getInputStream()){

                // Send data to server
                output.write(toServerBytes);

                // Receive data from server
                int bytesRead;
                while ((bytesRead = input.read(fromServerBuffer)) != -1) {
                    byteArrayOutputStream.write(fromServerBuffer, 0, bytesRead);
                }
                callback.onComplete(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                callback.onComplete(null);
            }
        });

        // Start both threads
        sendAndReceiveThread.start();

        return byteArrayOutputStream.toByteArray();
    }


}


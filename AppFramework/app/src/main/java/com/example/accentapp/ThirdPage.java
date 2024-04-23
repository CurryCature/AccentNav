package com.example.accentapp;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class ThirdPage extends AppCompatActivity{
    TextView textView;
    ImageView logo;
    Button button;

    private int port = 28561;

    private String hostname = "130.229.141.0";

    byte[] dataToSend = new byte[0];

    private long fileSizeInBytes = 0;

    byte[] responseData = new byte[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the Intent that started this activity
        Intent intent = getIntent();
        // Get the server response from the Intent
        String serverResponse = intent.getStringExtra("serverResponse");
        // Get the output file path from the Intent
        //String outputFilePath = intent.getStringExtra("outputFilePath");
        String outputFilePath = "/storage/emulated/0/Android/data/com.example.accentapp/files/Music/recording.mp4";

        // Find the TextView in which to display the server response
        TextView responseTextView = findViewById(R.id.responseTextView);

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
            dataToSend = Files.readAllBytes(Paths.get(outputFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            responseData = askServer(hostname, port, dataToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Set the server response as the text of the TextView
        responseTextView.setText(serverResponse);
    }



    private static final int BUFFERSIZE = 1024;

    public static byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // Create separate threads for sending and receiving
        Thread sendThread = new Thread(() -> {
            try (Socket clientSocket = new Socket(hostname, port);
                 OutputStream output = clientSocket.getOutputStream()) {
                output.write(toServerBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread receiveThread = new Thread(() -> {
            try (Socket clientSocket = new Socket(hostname, port);
                 InputStream input = clientSocket.getInputStream()) {
                int bytesRead;
                while ((bytesRead = input.read(fromServerBuffer)) != -1) {
                    byteArrayOutputStream.write(fromServerBuffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Start both threads
        sendThread.start();
        receiveThread.start();

        try {
            // Wait for both threads to finish
            sendThread.join();
            receiveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }


    /*public static void main(String[] args) {

        try {
            byte[] responseData = askServer(hostname, port, dataToSend);
            System.out.println("Received data from server: " + new String(responseData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}
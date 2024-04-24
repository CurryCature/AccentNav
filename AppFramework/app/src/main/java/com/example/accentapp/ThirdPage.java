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
import java.nio.ByteBuffer;

public class ThirdPage extends AppCompatActivity implements RepositoryCallback<byte[]>{
    TextView textView;
    ImageView logo;
    Button button;

    private int port = 28561;

    private String hostname = "130.229.144.97";

    byte[] dataToSend = new byte[0];

    byte[]dataToSendWithoutHeader = new byte[0];

    private long fileSizeInBytes = 0;

    byte[] responseData = new byte[0];

    TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Get the Intent that started this activity
        Intent intent = getIntent();
        // Get the output file path from the Intent
        //String outputFilePath = intent.getStringExtra("outputFilePath");
        String outputFilePath = "/storage/emulated/0/Android/data/com.example.accentapp/files/Music/recording.mp4";

        // Find the TextView in which to display the server response
        responseTextView = findViewById(R.id.responseTextView);

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
        try {
            askServer(hostname, port, dataToSend, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Set the server response as the text of the TextView
        //responseTextView.setText(serverResponse);
    }

    public void onComplete(byte[] result) {
        if (result != null) {
            responseData = result;
            // Convert the byte array to a JSON string
            String jasonString = new String(responseData);
            //Specify the file path
            String FilePath = getFilesDir() + "/response.json";
            //Create a file object
            File file = new File(FilePath);
            //Write the JSON string to the file
            try{
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(jasonString);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update the UI here
            //runOnUiThread ensure that UI update is performed on the main thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //responseTextView.setText(new String(responseData));
                    //Set up a path to save the audio file.
                    responseTextView.setText(FilePath);
                }
            });
        } else {
            // Handle error here
            responseTextView.setText("Error, your accent is not detected. Please try again.");
        }
    }

    private static final int BUFFERSIZE = 1024;

    public static byte[] askServer(String hostname, int port, byte[] toServerBytes, RepositoryCallback<byte[]> callback) throws IOException {


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // Create separate threads for sending and receiving
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

        try {
            // Wait for both threads to finish
            sendAndReceiveThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }


}

/*
* The askServer method is indeed responsible for sending data to the server
*  and receiving the response. However, it's important to note that the onComplete
*  method is a callback that gets executed when the server's response is received.
*  In the current setup, the askServer method is called in the onCreate method,
* which is correct because you want to initiate the server communication when the activity is created.
*  The askServer method then starts two threads, one for sending data to the server and another for receiving the response.
*   When the response is received, the onComplete method of the RepositoryCallback is called. This is where you handle the server's response.
*  In your case, you're updating the responseData variable and the UI in the onComplete method,
*  which is the correct place to do so.  So, to answer your question,
* the askServer method should not be called in the onComplete method.
*  It should be called where you want to initiate the server communication,
* which in your case is in the onCreate method.
* The onComplete method should be used to handle the server's response.
*  */
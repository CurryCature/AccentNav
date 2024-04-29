package com.example.accentapp;

import android.content.Intent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;
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

    private int port = 28591;

    private String hostname = "acucentapp.asuscomm.com";

    byte[] dataToSend = new byte[0];

    byte[]dataToSendWithoutHeader = new byte[0];

    private long fileSizeInBytes = 0;

    byte[] responseData = new byte[0];

    TextView responseTextView;

    private static final int BUFFERSIZE = 1024;

    String outputFilePath = "/storage/emulated/0/Android/data/com.example.accentapp/files/Music/recording.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

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

        /*timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Loading.this, ThirdPage.class);
                intent.putExtra("outputFilePath", outputFilePath);
                startActivity(intent);
                finish();
            }
        }, 2000);*/

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
        if (result != null) {
            responseData = result;
            // Convert the byte array to a JSON string
            String jasonString = new String(responseData);
            //Specify the file path
            String FilePath = getFilesDir() + "/response.json";
            //Create a file object
            File file = new File(FilePath);
            //Write the JSON string to the file
            try {
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
                      Intent intent = new Intent(Loading.this, Error.class);
                      startActivity(intent);
                      finish();
                  }
              }
            );
        }
    }

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

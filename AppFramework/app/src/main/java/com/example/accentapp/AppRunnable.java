package com.example.accentapp;
import android.os.Environment;

import java.net.*;
import java.io.*;
import java.nio.file.Files;

public class AppRunnable implements Runnable{

    byte[] serverAnswer = new byte[0];
    private byte[] userInputBytes = new byte[0];
    private long fileSizeInBytes;
    private int port;
    private String hostname;
    
    private String outputFilePath;

    private static int BUFFERSIZE = 1024;

    public AppRunnable(int port, String hostname, String outputFilePath) {
        this.port = port;
        this.hostname = hostname;
        this.outputFilePath = outputFilePath;
    }

    public void run(){
        
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
            //userInputBytes = Files.readAllBytes(Paths.get(outputFilePath));
            FileInputStream fileInputStream = new FileInputStream(audioFile);
            userInputBytes = new byte[(int) audioFile.length()];
            fileInputStream.read(userInputBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            serverAnswer = askServer(hostname, port, userInputBytes);
        }
        catch(IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }
        
    }
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        if(toServerBytes.length == 0){
            return(askServer(hostname,port,userInputBytes));
        }
        //use dynamic output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Pre-allocate byte buffers for receiving
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // TODO: Error handling

        try(Socket clientSocket = new Socket(hostname, port)){

            OutputStream output = clientSocket.getOutputStream();
            //sends the data to the server
            output.write(toServerBytes);

            InputStream input = clientSocket.getInputStream();

            int bytesRead;
            //reads the input from the server's input stream and writes it to the
            //dynamic array.
            while((bytesRead = input.read(fromServerBuffer))!= -1){
                byteArrayOutputStream.write(fromServerBuffer, 0, bytesRead);
            }

            clientSocket.close();
            output.flush();
        }

        return byteArrayOutputStream.toByteArray();

    }
}

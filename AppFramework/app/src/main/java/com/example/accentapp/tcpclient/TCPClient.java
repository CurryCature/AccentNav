package com.example.accentapp.tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    private static int BUFFERSIZE = 1024;
    
    public TCPClient() {
    }

    /*public byte[] askServer(String hostname, int port) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Pre-allocate byte buffers for receiving
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        // TODO: Error handling

        try(Socket clientSocket = new Socket(hostname, port)){
            InputStream input = clientSocket.getInputStream();

            int bytesRead;

            while((bytesRead = input.read(fromServerBuffer)) != -1){
                byteArrayOutputStream.write(fromServerBuffer, 0, bytesRead);
            }

        }

        return byteArrayOutputStream.toByteArray();

    }*/

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        /*if(toServerBytes.length == 0){
            return(askServer(hostname,port));
        }*/
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
            //dynamic aerray.
            while((bytesRead = input.read(fromServerBuffer))!= -1){
                byteArrayOutputStream.write(fromServerBuffer, 0, bytesRead);
            }

            clientSocket.close();
            output.flush();
        }

        return byteArrayOutputStream.toByteArray();


    }
}

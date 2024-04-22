package com.example.accentapp;

import java.net.*;
import java.io.*;
import com.example.accentapp.tcpclient.TCPClient;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TCPAsk {
    /*
     * Usage: explain how to use the program, then exit with failure status
     */
    private static void usage() {
        System.err.println("Usage: TCPAsk host port <data to server>");
        System.exit(1);
    }

    /*
     * Main program. Parse arguments on command line and call TCPClient
     */


    public static void main(String[] args) {
        String hostname = null;
        int port = 0;
        byte[] userInputBytes = new byte[0];

        try {
            // Get mandatory command line arguments: hostname and port number
            int argindex = 0;
            hostname = args[argindex++];
            port = Integer.parseInt(args[argindex++]);

            // Remaining arguments, if any, are string to send to server
            if (argindex < args.length) {
                // The next argument is assumed to be the path to the audio file
                String audioFilePath = args[argindex++];
                userInputBytes = Files.readAllBytes(Paths.get(audioFilePath));
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException ex) {
            // Exceeded array while parsing command line, or could
            // not convert port number argument to integer, or could not read the audio file
            usage();
        }

        try {
            TCPClient tcpClient = new com.example.accentapp.tcpclient.TCPClient();
            byte[] serverBytes  = tcpClient.askServer(hostname, port, userInputBytes);
            String serverOutput = new String(serverBytes);
            System.out.printf("%s:%d says:\n%s", hostname, port, serverOutput);
        } catch(IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }
}


package com.example.accentapp;

import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class TCPClient {

    private Socket socket;
    private DataOutputStream dataOutputStream;

    public void connect(String serverIp, int serverPort) throws IOException {
        socket = new Socket(serverIp, serverPort);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String message) {
        new SendMessageTask().execute(message);
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (dataOutputStream != null) {
            dataOutputStream.close();
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... messages) {
            try {
                dataOutputStream.writeUTF(messages[0]);
                dataOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

package org.example.client;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

@Setter
@Getter
public class ClientConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Consumer<String> listener;

    public ClientConnection(String host, int port){
        try {
            socket = new Socket(host, port);

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            out = new PrintWriter(socket.getOutputStream(), true);
        }catch (Exception e){}

        startListening();
    }

    public void send(String msg) {
        out.println(msg);
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (listener != null) {
                        listener.accept(msg);
                    }
                }
            } catch (Exception e) {
                System.out.println("Disconnected from server");
            }
        }).start();
    }
}

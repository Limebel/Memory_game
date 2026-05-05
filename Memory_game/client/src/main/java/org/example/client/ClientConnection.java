package org.example.client;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

@Setter
@Getter
public class ClientConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Consumer<String> listener;
    private GameSetupFrame frame;
    private int[] cards;
    private int height;
    private int width;
    private boolean[] states;

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
                    System.out.println("SERVER → " + msg);
                    switchView(msg);
                    if (listener != null) {
                        listener.accept(msg);
                    }
                }
            } catch (Exception e) {
                System.out.println("Disconnected from server");
            }
        }).start();
    }

    private void switchView(String msg){
        if(msg.startsWith("SIZE CHOICE")){
            SwingUtilities.invokeLater(frame::goSetup);
        }
        else if(msg.startsWith("INIT")){
            new Thread(() -> {
                    try {
                        String[] parts = msg.split(":");
                        height = Integer.parseInt(parts[1]);
                        width = Integer.parseInt(parts[2]);
                        String[] cardsStr = parts[3].split(",");
                        cards = new int[height * width];
                        for(int i = 0; i<(height*width); i++){
                            cards[i] = Integer.parseInt(cardsStr[i].trim());
                        }
                        frame.goBoard();
                    } catch (Exception e) {
                        System.out.println("Init text not parsed correctly");
                    }
            }).start();
        }
        else if(msg.startsWith("STATE")){
            new Thread(() -> {
                try {
                    String[] parts = msg.split(":");
                    height = Integer.parseInt(parts[1]);
                    width = Integer.parseInt(parts[2]);
                    String[] cardsStr = parts[3].split(",");
                    states = new boolean[height * width];
                    for(int i = 0; i<(height*width); i++){
                        states[i] = Boolean.parseBoolean(cardsStr[i].trim());
                    }
                    System.out.println("Successful parsing");
                    frame.goReload(states);
                    System.out.println("Reloaded game states");
                } catch (Exception e) {
                    System.out.println("State text not parsed fully");
                }
            }).start();
        }
    }
}

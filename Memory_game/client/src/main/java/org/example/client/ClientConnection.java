package org.example.client;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;

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

    private int index;
    private int yourScore = 0;
    private int opponentScore = 0;
    private String opponentName = "";
    private boolean boardReady = false;

    /**
     * Creates a client connection to the specified server and port.
     * Initializes input/output streams for communication and starts
     * a background listener for incoming server messages.
     *
     * @param host server address to connect to
     * @param port server port to connect to
     */
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

    /**
     * Sends a message to the server through the output stream.
     *
     * @param msg the message to send
     */
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
        else if(msg.startsWith("Player")){
            String[] parts = msg.split(" ");
            index = Integer.parseInt(parts[1]);
        }
        else if(msg.startsWith("INIT")) {
            try {
                String[] parts = msg.split(":");
                height = Integer.parseInt(parts[1]);
                width = Integer.parseInt(parts[2]);
                String[] cardsStr = parts[3].split(",");
                cards = new int[height * width];
                for (int i = 0; i < (height * width); i++) {
                    cards[i] = Integer.parseInt(cardsStr[i].trim());
                }
                SwingUtilities.invokeAndWait(() -> {
                    frame.goBoard();
                    boardReady = true;
                });
            } catch (Exception e) {
                System.out.println("Init text not parsed correctly");
            }
        }
        else if(msg.startsWith("STATE")) {
            if(!boardReady){
                System.out.println("Board not initialized yet");
                return;
            }
            try {
                String[] parts = msg.split(":");
                height = Integer.parseInt(parts[1]);
                width = Integer.parseInt(parts[2]);
                String[] cardsStr = parts[3].split(",");
                states = new boolean[height * width];
                for (int i = 0; i < (height * width); i++) {
                    states[i] = Boolean.parseBoolean(cardsStr[i].trim());
                }
                System.out.println("Successful parsing");
                frame.goReload(states);
                System.out.println("Reloaded game states");
            } catch (Exception e) {
                System.out.println("State text not parsed fully");
            }
        }
        else if(msg.startsWith("SCORE")) {
            try {
                String[] parts = msg.split(":");
                if (index == 0) {
                    yourScore = Integer.parseInt(parts[1]);
                    opponentScore = Integer.parseInt(parts[2]);
                } else if (index == 1) {
                    yourScore = Integer.parseInt(parts[2]);
                    opponentScore = Integer.parseInt(parts[1]);
                } else System.out.println("Incorrect index");
                frame.reloadStats();
            } catch (Exception e) {
                System.out.println("State text not parsed fully");
            }
        }
        else if(msg.startsWith("NAMES")) {
            try {
                String[] parts = msg.split(":");
                if (index == 0) {
                    opponentName = parts[2];
                } else if (index == 1) {
                    opponentName = parts[1];
                } else System.out.println("Incorrect index");
                frame.reloadStats();
                System.out.println("Your opponent's name is " + opponentName);
            } catch (Exception e) {
                System.out.println("State text not parsed fully");
            }
        }
        else if(msg.startsWith("FLIPPED")) {
            if(!boardReady){
                System.out.println("Board not initialized yet");
                return;
            }
            try {
                String[] parts = msg.split(":");
                int index = Integer.parseInt(parts[1]);
                System.out.println("Message about card flip successfully received");
                frame.handleCardFlip(index);
            } catch (Exception e) {
                System.out.println("Wrong flipped message received");
            }
        }
        else if(msg.startsWith("WON")){
            try {
                String[] parts = msg.split(":");
                if(parts[1].equals("TIE")){
                    frame.showFinish(2);
                    System.out.println("It's a tie!");
                }
                else if(index==Integer.parseInt(parts[1])){
                    frame.showFinish(0);
                    System.out.println("You won");
                }
                else{
                    frame.showFinish(1);
                    System.out.println("Game finished");
                }
            }catch(Exception e){
                    System.out.println("Wrong won message received");
                }
            }
        else if(msg.startsWith("CUSTOM MESSAGE")) {
            try {
                String[] parts = msg.split(":");
                System.out.println(parts[1]);
            } catch (Exception e) {
                System.out.println("Wrong won message received");
            }
        }
    }
}

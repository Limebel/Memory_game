package org.example.server;

import org.example.common.GameState;
import org.example.common.PlayerModel;

import java.io.*;
import java.net.Socket;

/**
 * Class responsible for handling connection with client.
 * Especially creating player instance for client
 * and sending and receiving messages
 */
public class ClientHandler implements Runnable{
    //client socket
    private final Socket socket;
    //game controller
    private final GameController controller;
    //player representation of client
    private PlayerModel player;

    //Input and output
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructor of Client Handler.
     * @param socket Client socket on server port
     * @param controller game controller
     */
    public ClientHandler(Socket socket, GameController controller) {
        this.socket = socket;
        this.controller = controller;
        controller.setChannel(this);
    }

    /**
     * Method to send message to client
     * @param msg message to be sent
     */
    public void send(String msg) {
        out.println(msg);
    }

    /**
     * Method run of Client Handler thread.
     * It is responsible for creating player instance for client
     * and listening for messages sent by client
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            send("WELCOME! Connected to server!");

            //Creating player instance
            player = new PlayerModel();
            String name = in.readLine(); //First message from Client is his name
            System.out.println(name);
            player.setName(name);
            player.setAdressIP(socket.getInetAddress());
            int num = controller.handleConnect(player);
            out.println("Player " + num + " connected");

            //Listening for messages in infinite loop and handling them appropriately
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);

                if(message.startsWith("SIZE")){
                    String[] parts = message.split(" ");
                    int width = Integer.parseInt(parts[2]);
                    int height = Integer.parseInt(parts[3]);
                    controller.startGame(width, height);
                }

                //Message confirming Client GUI is ready
                else if (message.equals("READY")) {
                    controller.handleReady();
                }

                //Message indicating which card client wants to flip
                else if (message.startsWith("FLIP")) {
                    try {
                        int index = Integer.parseInt(message.split(":")[1]);
                        if(!controller.handleFlip(player, index)){
                            //TODO:Maybe some more informative messages for client
                            send("Invalid FLIP: " + message);
                        }
                    }
                    catch(Exception e){
                        System.out.println("Invalid FLIP: " + message);
                    }
                }

                //In case of not recognizable message
                else {
                    System.out.println("Incorrect message: " + message);
                }
            }

        } catch (IOException e) {
            System.out.println("Player disconnected");
            controller.handleDisconnect(player);

        } finally {
            //At the end if player disconnects socket is closed
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
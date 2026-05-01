package org.example.server;

import org.example.common.PlayerModel;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket socket;
    private final GameController controller;
    private PlayerModel player;

    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, GameController controller) {
        this.socket = socket;
        this.controller = controller;
    }

    public void send(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);


            player = new PlayerModel();
            String name = in.readLine();
            player.setName(name);
            player.setAdressIP(socket.getInetAddress());

            // 🔑 THIS is where connectPlayer is used
            controller.handleConnect(player);
            out.println("CONNECTED");

            // 2. Listen for actions
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("READY")) {
                    controller.handleReady();
                }

                // Example protocol: "FLIP:3"
                if (message.startsWith("FLIP")) {
                    int index = Integer.parseInt(message.split(":")[1]);
                    controller.handleFlip(player, index);
                }
            }

        } catch (IOException e) {
            System.out.println("Player disconnected");
            // TODO: controller.handleDisconected();

        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
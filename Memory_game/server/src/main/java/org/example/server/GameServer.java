package org.example.server;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class GameServer {
    private final int port;
    private final GameController controller;

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            ClientHandler handler = new ClientHandler(clientSocket, controller);
            new Thread(handler).start();
        }
    }
}

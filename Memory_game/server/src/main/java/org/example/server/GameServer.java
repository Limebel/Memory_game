package org.example.server;

import lombok.AllArgsConstructor;
import org.example.common.GameModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class GameServer {
    private final int port;
    private final GameController controller;
    private List<ClientHandler> clients = new ArrayList<>();

    public GameServer(int port, GameController controller) {
        this.port = port;
        this.controller = controller;

        // 🔥 connect controller → server
        controller.setListener(new GameEventListener() {
            @Override
            public void onInit(GameModel game) {
                broadcastInit(game);
            }

            @Override
            public void onStateChanged(GameModel game) {
                //TODO:state update broadcast
                //broadcastState(game);
            }
        });
    }

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

    private void broadcastInit(GameModel game) {

        StringBuilder sb = new StringBuilder("INIT:");

        sb.append(game.getBoardHeight()).append(":");
        sb.append(game.getBoardWidth()).append(":");

        for (int i = 0; i < game.getCards().size(); i++) {
            sb.append(game.getCards().get(i).getValue());
            if (i < game.getCards().size() - 1) sb.append(",");
        }

        sendToAll(sb.toString());
    }

    private void sendToAll(String msg) {
        for (ClientHandler c : clients) {
            c.send(msg);
        }
    }
}

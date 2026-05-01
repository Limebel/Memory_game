package org.example.server;

import javax.swing.*;

public class ServerMain {
    public static void main(String[] args) {
        GameController controller = new GameController();
        GameServer server = new GameServer(1234, controller);
        new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
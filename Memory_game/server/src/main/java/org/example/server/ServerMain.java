package org.example.server;

import javax.swing.*;

public class ServerMain {
    public static void main(String[] args) {
        GameServer server = new GameServer(1234);
        new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
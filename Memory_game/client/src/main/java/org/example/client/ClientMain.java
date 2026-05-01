package org.example.client;

import org.example.client.view.ColorManager;

import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) {
        ClientConnection connection = new ClientConnection("localhost", 1234);
        //connection.send("Player_" + System.currentTimeMillis());

        SwingUtilities.invokeLater(() -> {
            ColorManager.darkTheme();
            GameSetupFrame frame = new GameSetupFrame(connection);
            frame.setVisible(true);
        });
    }
}

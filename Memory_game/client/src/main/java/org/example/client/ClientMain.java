package org.example.client;

import org.example.client.view.ColorManager;

import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ColorManager.darkTheme();
            ClientConnection connection = new ClientConnection("192.168.0.33", 1234);
            GameSetupFrame frame = new GameSetupFrame(connection);
            frame.setVisible(true);
        });
    }
}

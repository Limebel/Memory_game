package org.example.client;

import org.example.client.view.ColorManager;

import javax.swing.*;

public class ClientMain {
    /**
     * Application entry point.
     * Initializes the UI thread, applies the dark theme,
     * creates a client connection to the server, and launches the initial game window.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ColorManager.darkTheme();
            ClientConnection connection = new ClientConnection("192.168.1.114", 1234); // Change IP
            GameSetupFrame frame = new GameSetupFrame(connection);
            frame.setVisible(true);
        });
    }
}

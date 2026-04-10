package org.example;

import javax.swing.*;
import org.example.view.ColorManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ColorManager.darkTheme();
            GameSetupFrame frame = new GameSetupFrame();
            frame.setVisible(true);
        });
    }
}
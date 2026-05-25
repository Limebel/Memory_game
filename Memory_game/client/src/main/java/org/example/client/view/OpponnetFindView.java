package org.example.client.view;

import org.example.common.GameModel;
import org.example.common.PlayerModel;

import javax.swing.*;
import java.awt.*;

/**
 * Creates the "waiting for opponent" view.
 */
public class OpponnetFindView  extends JPanel {

    /**
     * Creates the "waiting for opponent" view.
     * Displays a simple message while the client is waiting for a match to start.
     *
     * @param onStart callback that would normally be triggered when starting/connecting
     */
    public OpponnetFindView(Runnable onStart) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        Font bigFont = new Font("SansSerif", Font.PLAIN, 36);

        JLabel label = new JLabel("Wait for the opponent...");
        label.setFont(bigFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);
    }
}
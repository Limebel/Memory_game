package org.example.client.view;

import org.example.common.GameModel;
import org.example.common.PlayerModel;
import org.example.client.ClientConnection;

import javax.swing.*;
import java.awt.*;

public class StartView extends JPanel {
    JTextField nameField;
    JButton start;
    ClientConnection connection;

    /**
     * Creates the start screen where the user enters a nickname and connects to the server.
     * Displays an input field and a connect button, both triggering the start action.
     *
     * @param onStart     callback executed when the user attempts to connect
     * @param connection  client-server connection used for game communication
     */
    public StartView(Runnable onStart, ClientConnection connection) {
        this.connection = connection;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);

        Font bigFont = new Font("SansSerif", Font.PLAIN, 36);

        JLabel label = new JLabel("Enter your nickname:");
        label.setFont(bigFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        nameField = new JTextField(20);
        nameField.setFont(bigFont);
        nameField.setPreferredSize(new Dimension(200,50));
        gbc.gridy = 1;
        add(nameField, gbc);

        start = new JButton("Connect");
        start.setFont(bigFont);
        start.setPreferredSize(new Dimension(300, 70));
        gbc.gridy = 3;
        add(start, gbc);

        start.addActionListener(e -> submit(onStart));
        nameField.addActionListener(e -> submit(onStart));

    }
    private void submit(Runnable onStart) {
        String name = nameField.getText();
        GameModel.getInstance().getPlayers().add(new PlayerModel());
        GameModel.getInstance().getPlayers().get(0).setName(name);
        connection.send(name);
        onStart.run();
    }
}
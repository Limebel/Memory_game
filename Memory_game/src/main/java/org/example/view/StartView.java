package org.example.view;

import org.example.DataStorage;

import javax.swing.*;
import java.awt.*;

public class StartView extends JPanel {
    JTextField nameField;
    JButton start;

    public StartView(Runnable onStart) {
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
        DataStorage.getInstance().setThisNickname(nameField.getText());
        onStart.run();
    }
}
package org.example.client.view;

import org.example.client.ClientConnection;
import org.example.common.GameModel;

import javax.swing.*;
import java.awt.*;

public class SetupView extends JPanel {
    JSlider widthSlider;
    JSlider heightSlider;
    private JButton confirmButton;
    private ClientConnection connection;

    public SetupView(Runnable onConfirm, ClientConnection connection) {
        this.connection = connection;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);

        Font bigFont = new Font("SansSerif", Font.PLAIN, 36);

        widthSlider = new JSlider(2, 12, 6) {
            @Override
            public void setValue(int n) {
                if (n % 2 != 0) n--; // force even
                super.setValue(n);
            }
        };
        heightSlider = new JSlider(2,8,5);

        widthSlider.setMajorTickSpacing(2);
        widthSlider.setPaintTicks(true);
        widthSlider.setPaintLabels(true);
        heightSlider.setSnapToTicks(true);

        heightSlider.setMajorTickSpacing(1);
        heightSlider.setPaintTicks(true);
        heightSlider.setPaintLabels(true);
        heightSlider.setSnapToTicks(true);

        gbc.gridx = 0; gbc.gridy = 0;
        add(widthSlider, gbc);

        gbc.gridy = 1;
        add(heightSlider, gbc);

        confirmButton = new JButton("Confirm");
        confirmButton.setFont(bigFont);
        gbc.gridy = 2;
        add(confirmButton, gbc);

        confirmButton.addActionListener(e -> submit(onConfirm));
    }
    private void submit(Runnable onConfirm) {
        int width = widthSlider.getValue();
        int height = heightSlider.getValue();
        connection.send("SIZE IS "+width+" "+height);
        GameModel.getInstance().setBoardWidth(width);
        GameModel.getInstance().setBoardHeight(height);
        //onConfirm.run();
    }

}
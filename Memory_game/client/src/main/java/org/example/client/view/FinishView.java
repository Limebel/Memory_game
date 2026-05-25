package org.example.client.view;

import org.example.client.ClientConnection;
import org.example.client.GameSetupFrame;
import org.example.common.GameModel;
import org.example.common.GameState;
import org.example.common.PlayerModel;

import javax.swing.*;
import java.awt.*;

public class FinishView extends JPanel {
    JTextField nameField;
    JButton start;
    Color highlight = new Color(255, 82, 140);
    GameSetupFrame frame;


    public FinishView(ClientConnection conn, int textVariant, GameSetupFrame frame) {
        this.frame = frame;
        String text = "";
        String subtext = "";

        if(textVariant==0){
            text = "You won!!!";
            subtext = "Congratulations!";
        }
        else if (textVariant==1) {
            text = "Game over!";
            subtext = conn.getOpponentName() + " won.";
        }
        else if (textVariant==2) {
            text = "It's a tie!";
            subtext = "Good game :)";
        }

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);

        Font bigFont = new Font("SansSerif", Font.PLAIN, 50);

        JLabel label = new JLabel(text);
        label.setFont(bigFont);
        label.setForeground(highlight);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        Font smallFont = new Font("SansSerif", Font.PLAIN, 36);

        JLabel sublabel = new JLabel(subtext);
        sublabel.setFont(smallFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(sublabel, gbc);

        start = new JButton("New game");
        start.setFont(smallFont);
        start.setPreferredSize(new Dimension(300, 70));
        gbc.gridy = 3;
        add(start, gbc);

        start.addActionListener(e -> submit(conn));
    }
    private void submit(ClientConnection conn) {
        conn.send("NG:"+conn.getIndex());
        frame.goWait();
    }
}
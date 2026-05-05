package org.example.client.view;

import javax.swing.*;
import java.awt.*;

public class Card extends JButton {
    private int id;
    private int pair;
    private Icon front;
    private Icon back = null;
    private boolean revealed = false;
    private int count = 0; // FORT TESTING ONLY

    public Card(int id, int iconNr) {
        String path = String.format("/card_fronts/" + (iconNr+1) + ".png");
        this.front = new ImageIcon(getClass().getResource(path));
        this.id = id;
        this.pair = iconNr;

        setIcon(back); // 👈 important default state
    }

    public void flip() {
        setFocusPainted(false);
        setBorderPainted(false);
        count++;
        if(!revealed){
            setIcon(front);
            setBackground(new java.awt.Color(128, 128, 128)); // light gray, "light" in CM
        }
        else{
            setIcon(back);
            setBackground(new java.awt.Color(82, 255, 197)); // mint, "base" in CM
        }
        revealed = !revealed;
        if(count>1)setPickedUp();
    }

    public void setPickedUp() { // card picked up as in no longer available
        setIcon(null);
        setEnabled(false); // prevents clicks
        setBorderPainted(false);
        setContentAreaFilled(false);
    }
}
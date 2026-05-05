package org.example.client.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class Card extends JButton {
    private int id;
    private int pair;
    private Icon front;
    private Icon back = null;
    private boolean status = true;
    private boolean revealed = false;

    public Card(int id, int iconNr) {
        String path = String.format("/card_fronts/" + (iconNr+1) + ".png");
        this.front = new ImageIcon(getClass().getResource(path));
        this.id = id;
        this.pair = iconNr;

        setIcon(back); // important default state
    }

    public synchronized void flip() {
        if(status){
            return;
        }
        System.out.println("Card status: "+status);
        System.out.println("Card flipped: "+id);
        setFocusPainted(false);
        setBorderPainted(false);
        if(!revealed){
            setIcon(front);
            setBackground(new java.awt.Color(128, 128, 128)); // light gray, "light" in CM
        }
        else{
            setIcon(back);
            setBackground(new java.awt.Color(82, 255, 197)); // mint, "base" in CM
        }
        revealed = !revealed;
    }

    public synchronized void setPickedUp() { // card picked up as in no longer available
        setIcon(null);
        setEnabled(false); // prevents clicks
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    public boolean getStatus() {
        return status;
    }
}
package org.example.client.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
public class Card extends JButton {
    private int id;
    private int pair;
    private Icon front;
    private Icon back = null;
    private boolean status = true;
    private boolean revealed = false;

    /**
     * Creates a card with a unique id and an assigned pair value (icon number).
     * Loads the front image from resources and sets the initial state to the back side.
     *
     * @param id     unique identifier for the card
     * @param iconNr determines which image is used for the card's front and defines its matching pair
     */
    public Card(int id, int iconNr) {
        String path = String.format("/card_fronts/" + (iconNr+1) + ".png");
        this.front = new ImageIcon(getClass().getResource(path));
        this.id = id;
        this.pair = iconNr;

        setIcon(back); // important default state
    }

    /**
     * Flips the card between its front and back visual states.
     * If the card is already locked/disabled (status = true), the method exits early.
     * Updates icon, background color, and toggles the revealed state.
     * Also logs basic debug information to the console.
     */
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

    /**
     * Marks the card as permanently selected/matched.
     * Disables interaction and removes all visual content to indicate it is no longer available.
     */
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
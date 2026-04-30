package org.example.client.view;

import javax.swing.*;

public class Card extends JButton {
    private Icon front, back;
    private boolean revealed = false;

    public Card(Icon front, Icon back) {
        super(back);
        this.front = front;
        this.back = back;
    }

    public void flip() {
        if(!revealed) setIcon(front);
        else setIcon(back);
        revealed = !revealed;
    }
}
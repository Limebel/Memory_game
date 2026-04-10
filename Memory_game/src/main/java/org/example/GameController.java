package org.example;

import org.example.view.Card;

import javax.swing.*;

// Stub for actual game logic
public class GameController {
    private int rows, cols;
    private int score = 0;

    public GameController(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public boolean canFlip(Card card) {
        return true; // TODO: implement rules
    }

    public void processMove(Card card) {
        score++; // TODO: implement match checking, moves, etc.
    }

    public int getScore() {
        return score;
    }

    public Icon getCardFront(int index) {
        return UIManager.getIcon("OptionPane.informationIcon"); // placeholder
    }

    public Icon getCardBack() {
        return UIManager.getIcon("OptionPane.warningIcon"); // placeholder
    }
}
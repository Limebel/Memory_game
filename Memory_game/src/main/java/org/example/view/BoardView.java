package org.example.view;

import org.example.DataStorage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardView extends JPanel {
    int rows;
    int cols;

    private StatsPanel statsPanel;
    private JPanel cardsPanel;
    private JPanel controlPanel;

    private List<CardButton> cards;
    private GameController gameController;

    public BoardView() {
        rows = DataStorage.getInstance().getBoardHeight();
        cols = DataStorage.getInstance().getBoardWidth();

        setLayout(new BorderLayout(10,10));

        // Game logic
        gameController = new GameController(rows, cols);

        // Stats panel (top)
        statsPanel = new StatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        // Cards panel (center)
        cardsPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        cards = new ArrayList<>();
        for(int i=0; i<rows*cols; i++) {
            CardButton card = new CardButton(gameController.getCardFront(i), gameController.getCardBack());
            card.addActionListener(e -> handleCardClick(card));
            cards.add(card);
            cardsPanel.add(card);
        }
        add(cardsPanel, BorderLayout.CENTER);

        // Control panel (bottom)
        controlPanel = new JPanel();
        JButton leaveButton = new JButton("Leave Game");
        leaveButton.addActionListener(e -> handleLeave());
        JButton specialButton = new JButton("Special Move");
        specialButton.addActionListener(e -> handleSpecial());
        controlPanel.add(leaveButton);
        controlPanel.add(specialButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void handleCardClick(CardButton card) {
        if(gameController.canFlip(card)) {
            card.flip();
            gameController.processMove(card);
            statsPanel.refresh(gameController);
        }
    }

    private void handleLeave() {
        // TODO: go back to start menu
        System.out.println("Leave clicked");
    }

    private void handleSpecial() {
        // TODO: handle special move
        System.out.println("Special clicked");
    }

    // --- Inner classes for example ---

    private static class CardButton extends JButton {
        private Icon front, back;
        private boolean revealed = false;

        public CardButton(Icon front, Icon back) {
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

    private static class StatsPanel extends JPanel {
        private JLabel scoreLabel;

        public StatsPanel() {
            scoreLabel = new JLabel("Score: 0");
            add(scoreLabel);
        }

        public void refresh(GameController game) {
            scoreLabel.setText("Score: " + game.getScore());
        }
    }

    // Stub for actual game logic
    private static class GameController {
        private int rows, cols;
        private int score = 0;

        public GameController(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }

        public boolean canFlip(CardButton card) {
            return true; // TODO: implement rules
        }

        public void processMove(CardButton card) {
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
}
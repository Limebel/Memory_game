package org.example.view;

import org.example.DataStorage;
import org.example.GameController;

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

    private List<Card> cards;
    private GameController gameController;

    public BoardView(Runnable onConfirm){
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
            Card card = new Card(gameController.getCardFront(i), gameController.getCardBack());
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

    private void handleCardClick(Card card) {
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


}
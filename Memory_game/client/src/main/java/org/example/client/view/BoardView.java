package org.example.client.view;

import org.example.client.ClientConnection;
import org.example.common.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardView extends JPanel {
    private StatsPanel statsPanel;
    private JPanel cardsPanel;
    private JPanel controlPanel;

    private List<Card> cards;
    private ClientConnection connection;
    private int rows;
    private int cols;

    public BoardView(ClientConnection con/* ,Runnable onConfirm*/){
        setLayout(new BorderLayout(10,10));
        rows = GameModel.getInstance().getBoardHeight();
        cols = GameModel.getInstance().getBoardWidth();

        // Game logic
        connection = con;

        // Stats panel (top)
        statsPanel = new StatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        // Cards panel (center)
        /*cardsPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        cards = new ArrayList<>();
        int numCards = rows*cols;
        for(int i=0; i<numCards/2; i++) {
            for(int j=0; j<2; j++){
                Card card = new Card(i*2+j, i+1); // passing card id (2i+j) and image number (i+1)
                // card.addActionListener(e -> handleCardClick(card)); //TODO CARD CLICK
                cards.add(card);
            }
        }
        Collections.shuffle(cards);
        for(Card card : cards){
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
        add(controlPanel, BorderLayout.SOUTH);*/
    }

    public void handleServerMessage(String msg) {
        if (msg.startsWith("INIT")) {
            handleInit(msg);
        }

        if (msg.startsWith("STATE")) {
            // TODO: Update board state
            //handleState(msg);
        }

        // TODO:Other messages handling
    }

    private void handleInit(String msg) {

        String[] parts = msg.split(":");

        rows = Integer.parseInt(parts[1]);
        cols = Integer.parseInt(parts[2]);

        String[] values = parts[3].split(",");

        SwingUtilities.invokeLater(() -> {

            cardsPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
            cards = new ArrayList<>();
            int numCards = rows*cols;
            for(int i=0; i<numCards/2; i++) {
                for(int j=0; j<2; j++){
                    Card card = new Card(i*2+j, i+1); // passing card id (2i+j) and image number (i+1)
                    // card.addActionListener(e -> handleCardClick(card)); //TODO CARD CLICK
                    cards.add(card);
                }
            }
            Collections.shuffle(cards);
            for(Card card : cards){
                cardsPanel.add(card);
            }
            add(cardsPanel, BorderLayout.CENTER);

            // Control panel (bottom)
            /*controlPanel = new JPanel();
            JButton leaveButton = new JButton("Leave Game");
            leaveButton.addActionListener(e -> handleLeave());
            JButton specialButton = new JButton("Special Move");
            specialButton.addActionListener(e -> handleSpecial());
            controlPanel.add(leaveButton);
            controlPanel.add(specialButton);
            add(controlPanel, BorderLayout.SOUTH);*/

            // 🔥 tell server we're ready
            connection.send("READY");
        });
    }

    public Icon getCardFront(int index) {
        return UIManager.getIcon("OptionPane.informationIcon"); // placeholder
    }

    public Icon getCardBack() {
        return UIManager.getIcon("OptionPane.warningIcon"); // placeholder
    }

    private void handleCardClick(Card card) {
        boolean allowed = true;
        if(allowed) {
            card.flip();
            //controller.processMove(card);
            statsPanel.refresh(/*controller*/);
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
        private GameModel gameData = GameModel.getInstance();

        public StatsPanel() {
            gameData.getPlayers().get(0).setScore(0);
            gameData.getPlayers().get(1).setScore(0);
            scoreLabel = new JLabel(gameData.getPlayers().get(0).getName() + "'s score: " + gameData.getPlayers().get(0).getScore() + "          " +
                    gameData.getPlayers().get(1).getName() + "'s score: " + gameData.getPlayers().get(1).getScore());
            add(scoreLabel);
        }

        public void refresh(/*GameController game*/) {
            scoreLabel.setText(gameData.getPlayers().get(0).getName() + "'s score: " + gameData.getPlayers().get(0).getScore() + "          " +
                    gameData.getPlayers().get(1).getName() + "'s score: " + gameData.getPlayers().get(1).getScore());
        }
    }

}
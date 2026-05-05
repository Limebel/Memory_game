package org.example.client.view;

import lombok.Getter;
import lombok.Setter;
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

    @Getter
    private List<Card> cards;
    private ClientConnection connection;
    private int rows;
    private int cols;

    public BoardView(ClientConnection con/* ,Runnable onConfirm*/, int rows, int cols, int[] pairings){
        setLayout(new BorderLayout(10,10));

        // Game logic
        connection = con;

        // Stats panel (top)
        statsPanel = new StatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        SwingUtilities.invokeLater(() -> {

            cardsPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
            cards = new ArrayList<>();
            int numCards = rows*cols;
            for(int i=0; i<numCards; i++) {
                Card card = new Card(i, pairings[i]);
                card.addActionListener(e -> handleCardClick(card)); //TODO CARD CLICK
                cards.add(card);
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

    public void handleServerMessage(String msg) {

        if (msg.startsWith("STATE")) {
            // TODO: Update board state
            //handleState(msg);
        }

        // TODO:Other messages handling
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
            scoreLabel = new JLabel("Statistics will go here");
//            gameData.getPlayers().get(0).setScore(0);
//            gameData.getPlayers().get(1).setScore(0);
//            scoreLabel = new JLabel(gameData.getPlayers().get(0).getName() + "'s score: " + gameData.getPlayers().get(0).getScore() + "          " +
//                    gameData.getPlayers().get(1).getName() + "'s score: " + gameData.getPlayers().get(1).getScore());
            add(scoreLabel);
        }

        public void refresh(/*GameController game*/) {
//            scoreLabel.setText(gameData.getPlayers().get(0).getName() + "'s score: " + gameData.getPlayers().get(0).getScore() + "          " +
//                    gameData.getPlayers().get(1).getName() + "'s score: " + gameData.getPlayers().get(1).getScore());
            scoreLabel = new JLabel("Statistics will go here!");
        }
    }

}
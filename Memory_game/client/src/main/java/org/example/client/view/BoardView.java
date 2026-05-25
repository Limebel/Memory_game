package org.example.client.view;

import lombok.Getter;
import org.example.client.ClientConnection;
import org.example.common.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game screen
 */
public class BoardView extends JPanel {
    private StatsPanel statsPanel; // line with scores display
    private JPanel cardsPanel; // board with cards

    @Getter
    private List<Card> cards;
    private ClientConnection connection;

    /**
     * Creates the main game board view.
     * Initializes the layout, stats panel, and card grid,
     * and notifies the server that the client is ready.
     *
     * @param con      the client-server connection used for communication with the game server
     * @param rows     number of rows in the card grid
     * @param cols     number of columns in the card grid
     * @param pairings array defining card pair relationships
     */
    public BoardView(ClientConnection con, int rows, int cols, int[] pairings) {
        setLayout(new BorderLayout(10, 10));

        // Game logic
        connection = con;

        // Stats panel (top)
        statsPanel = new StatsPanel(connection);
        add(statsPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        cards = new ArrayList<>();
        int numCards = rows * cols;
        for (int i = 0; i < numCards; i++) {
            Card card = new Card(i, pairings[i]);
            card.addActionListener(e -> handleCardClick(card)); //TODO CARD CLICK
            cards.add(card);
            cardsPanel.add(card);
        }
        add(cardsPanel, BorderLayout.CENTER);

        // 🔥 tell server we're ready
        connection.send("READY");
    }

    private void handleCardClick(Card card) {
        connection.send("FLIP:"+card.getId());
    }

    /**
     * Refreshes the statistics panel UI.
     * This updates any displayed game-related values such as score, moves, or timers
     * to match the current internal game state.
     */
    public void refreshStats(){
        statsPanel.refresh();
    }

    private static class StatsPanel extends JPanel {
        private JLabel yourScoreLabel;
        private JLabel opponentScoreLabel;
        private GameModel gameData = GameModel.getInstance();
        private ClientConnection conn;
        Color highlight = new Color(255, 82, 140);

        /**
         * Creates the statistics panel that displays both players' scores.
         * Initializes labels for the local player and the opponent using data from the server connection,
         * applies styling (font, color, layout), and positions them on opposite sides of the panel.
         *
         * @param conn the client-server connection used to retrieve player names and scores
         */
        public StatsPanel(ClientConnection conn) {
            this.conn = conn;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            yourScoreLabel = new JLabel("You: " + conn.getYourScore());
            opponentScoreLabel = new JLabel(conn.getOpponentName() + ": " + conn.getOpponentScore());

            yourScoreLabel.setFont(yourScoreLabel.getFont().deriveFont(Font.BOLD, 24f));
            opponentScoreLabel.setFont(opponentScoreLabel.getFont().deriveFont(Font.BOLD, 24f));

            yourScoreLabel.setForeground(highlight);
            opponentScoreLabel.setForeground(highlight);

            add(yourScoreLabel, BorderLayout.WEST);
            add(opponentScoreLabel, BorderLayout.EAST);
        }

        /**
         * Updates the displayed scores for both the local player and the opponent.
         * Pulls the latest values from the server connection and refreshes the UI labels.
         */
        public void refresh() {
            yourScoreLabel.setText("You: " + conn.getYourScore());
            opponentScoreLabel.setText(conn.getOpponentName() + ": " + conn.getOpponentScore());
        }
    }

}
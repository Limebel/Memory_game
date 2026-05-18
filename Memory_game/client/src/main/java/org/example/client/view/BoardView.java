package org.example.client.view;

import lombok.Getter;
import lombok.Setter;
import org.example.client.ClientConnection;
import org.example.common.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardView extends JPanel {
    private StatsPanel statsPanel;
    private JPanel cardsPanel;
    private JPanel controlPanel;

    @Getter
    private List<Card> cards;
    private ClientConnection connection;
    private int rows;
    private int cols;

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
        connection.send("FLIP:"+card.getId());
    }

    public void refreshStats(){
        statsPanel.refresh();
    }

    private static class StatsPanel extends JPanel {
        private JLabel yourScoreLabel;
        private JLabel opponentScoreLabel;
        private GameModel gameData = GameModel.getInstance();
        private ClientConnection conn;
        Color highlight = new Color(255, 82, 140);


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

        public void refresh() {
            yourScoreLabel.setText("You: " + conn.getYourScore());
            opponentScoreLabel.setText(conn.getOpponentName() + ": " + conn.getOpponentScore());
        }
    }

}
package org.example.client;

import lombok.Setter;
import org.example.client.view.*;

import javax.swing.*;
import java.awt.*;

public class GameSetupFrame extends JFrame{
    private SetupView setupView;
    private StartView startView;
    private OpponnetFindView opFindView;
    private BoardView boardView;
    private FinishView finishView;

    private CardLayout cl;
    private JPanel main;
    @Setter
    private ClientConnection connection;

    /**
     * Creates the main application window for the game setup flow.
     * Stores the client connection, links it to this frame, and initializes
     * the UI with default window settings.
     *
     * @param connection client-server connection used throughout the game
     */
    public GameSetupFrame(ClientConnection connection) {
        this.connection = connection;
        connection.setFrame(this);

        setTitle("Memory Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        cl = new CardLayout();
        main = new JPanel(cl);

        startView = new StartView(() -> cl.show(main, "WAIT"), connection);
        opFindView = new OpponnetFindView(() -> cl.show(main, "SETUP"));
        setupView = new SetupView(() -> onBoardConfirm(cl, main), connection);

        main.add(startView, "START");
        main.add(opFindView, "WAIT");
        main.add(setupView, "SETUP");

        add(main);
    }

    private void onBoardConfirm(CardLayout cl, JPanel main) {
        boardView = new BoardView(connection, connection.getHeight(), connection.getWidth(), connection.getCards());
        main.add(boardView, "BOARD"); // add it dynamically
        cl.show(main, "BOARD");
    }

    /**
     * Displays the finish screen for the game.
     * Creates a new finish view based on the result variant,
     * adds it to the main layout, and switches the UI to it.
     *
     * @param variant determines which end-screen message to show:
     *                0 = win, 1 = loss, 2 = tie
     */
    public void showFinish(int variant) {
        finishView = new FinishView(connection, variant);
        main.add(finishView, "FINISH"); // added dynamically
        cl.show(main, "FINISH");
    }

    /**
     * Switches the UI back to the start screen.
     * Uses the layout manager to display the START view.
     */
    public void goStart() {
        cl.show(main, "START");
    }

    /**
     * Switches the UI to the setup screen where board size is configured.
     */
    public void goSetup() {
        cl.show(main, "SETUP");
    }

    /**
     * Handles a card flip event triggered by a player action or game logic.
     * Retrieves the card at the given index and flips its visual state.
     *
     * @param index position of the card in the board's card list
     */
    public synchronized void handleCardFlip(int index){
        Card card = boardView.getCards().get(index);
        card.flip();
    }

    /**
     * Switches the UI to the game board view by triggering the board setup callback.
     */
    public void goBoard() {
        onBoardConfirm(cl, main);
    }

    /**
     * Refreshes the game statistics displayed on the board view.
     * Typically used after score or game state updates.
     */
    public void reloadStats(){
        boardView.refreshStats();
    }

    /**
     * Switches the UI to the waiting screen while the opponent is connecting / changing settings.
     */
    public void goWait(){
        cl.show(main, "WAIT");
    }

    /**
     * Reloads the board state based on the received (from server) status array.
     * Updates each card’s status and marks cards as picked up if they are no longer active.
     *
     * @param states array representing the current status of each card
     */
    public synchronized void goReload(boolean[] states){
        for (int i=0; i < states.length; i++){
            Card card = boardView.getCards().get(i);
            card.setStatus(states[i]);
            if(card.getStatus()){
                card.setPickedUp();
            }
        }
    }
}
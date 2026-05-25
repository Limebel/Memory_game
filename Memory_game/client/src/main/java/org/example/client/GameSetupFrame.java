package org.example.client;

import lombok.Getter;
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

    public void showFinish(int variant) {
        finishView = new FinishView(connection, variant, this);
        main.add(finishView, "FINISH"); // added dynamically
        cl.show(main, "FINISH");
    }

    public void goStart() {
        cl.show(main, "START");
    }

    public void goSetup() {
        cl.show(main, "SETUP");
    }

    public synchronized void handleCardFlip(int index){
        Card card = boardView.getCards().get(index);
        card.flip();
    }

    public void goBoard() {
        onBoardConfirm(cl, main);
    }

    public void reloadStats(){
        boardView.refreshStats();
    }

    public void goWait(){
        cl.show(main, "WAIT");
    }

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
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
        boardView = new BoardView(connection/*, () -> cl.show(main, "START")*/, connection.getHeight(), connection.getWidth(), connection.getCards());
        //TODO:Linia poniżej jest bardzo ważna (na żółto, żeby było widoczne
        //connection.setListener(boardView::handleServerMessage);
        main.add(boardView, "BOARD"); // add it dynamically
        cl.show(main, "BOARD");

        /*String name = GameModel.getInstance().getPlayers().get(0).getName();
        String opName = GameModel.getInstance().getPlayers().get(1).getName();
        int w = GameModel.getInstance().getBoardWidth();
        int h = GameModel.getInstance().getBoardHeight();*/

        /*JOptionPane.showMessageDialog(this,
                "Player: " + name + "\nOpponent: " + opName + "\nSize: " + w + "x" + h);*/
    }

    public void goStart() {
        cl.show(main, "START");
    }

    public void goSetup() {
        cl.show(main, "SETUP");
    }

    public void goBoard() {
        onBoardConfirm(cl, main);
        //cl.show(main, "BOARD");
    }

    public void goWait(){
        cl.show(main, "WAIT");
    }
}
package org.example.client;

import org.example.client.view.*;

import javax.swing.*;
import java.awt.*;

public class GameSetupFrame extends JFrame{
    private SetupView setupView;
    private StartView startView;
    private OpponnetFindView opFindView;
    private BoardView boardView;

    private ClientConnection connection;

    public GameSetupFrame(ClientConnection connection) {
        setTitle("Memory Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.connection = connection;
        initUI();
    }

    private void initUI() {
        CardLayout cl = new CardLayout();
        JPanel main = new JPanel(cl);

        startView = new StartView(() -> cl.show(main, "OP_FIND"));
        opFindView = new OpponnetFindView(() -> cl.show(main, "SETUP"));
        setupView = new SetupView(() -> onBoardConfirm(cl, main));

        main.add(startView, "START");
        main.add(opFindView, "OP_FIND");
        main.add(setupView, "SETUP");

        add(main);
    }

    private void onBoardConfirm(CardLayout cl, JPanel main) {
        boardView = new BoardView(connection/*, () -> cl.show(main, "START")*/);
        //TODO:Linia poniżej jest bardzo ważna (na żółto, żeby było widoczne
        connection.setListener(boardView::handleServerMessage);
        main.add(boardView, "BOARD"); // add it dynamically
        cl.show(main, "BOARD");

        /*String name = GameModel.getInstance().getPlayers().get(0).getName();
        String opName = GameModel.getInstance().getPlayers().get(1).getName();
        int w = GameModel.getInstance().getBoardWidth();
        int h = GameModel.getInstance().getBoardHeight();*/

        /*JOptionPane.showMessageDialog(this,
                "Player: " + name + "\nOpponent: " + opName + "\nSize: " + w + "x" + h);*/
    }
}
package org.example;

import org.example.view.*;

import javax.swing.*;
import java.awt.*;

public class GameSetupFrame extends JFrame{
    private SetupView setupView;
    private StartView startView;
    private OpponnetFindView opFindView;
    private BoardView boardView;


    public GameSetupFrame() {
        setTitle("Memory Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        boardView = new BoardView(() -> cl.show(main, "START"));
        main.add(boardView, "BOARD"); // add it dynamically
        cl.show(main, "BOARD");

        String name = DataStorage.getInstance().getThisNickname();
        String opName = DataStorage.getInstance().getOpponentNickname();
        int w = DataStorage.getInstance().getBoardWidth();
        int h = DataStorage.getInstance().getBoardHeight();

        JOptionPane.showMessageDialog(this,
                "Player: " + name + "\nOpponent: " + opName + "\nSize: " + w + "x" + h);
    }
}
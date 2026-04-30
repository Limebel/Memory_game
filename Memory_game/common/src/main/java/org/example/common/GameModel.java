package org.example.common;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class GameModel {
    private static final GameModel instance = new GameModel();

    private ArrayList<PlayerModel> players;
    //private PlayerModel opponentPlayer;
    private ArrayList<CardModel> cards;
    private GameState state;
    private int currentPlayer;
    private ArrayList<CardModel> chosenCards;

    private int boardWidth;
    private int boardHeight;

    public static GameModel getInstance() {
        return instance;
    }

}
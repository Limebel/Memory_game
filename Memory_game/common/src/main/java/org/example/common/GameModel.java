package org.example.common;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class GameModel {
    private static final GameModel instance = new GameModel();

    private ArrayList<PlayerModel> players = new ArrayList<>();
    private ArrayList<CardModel> cards = new ArrayList<>();
    private GameState state;
    private int currentPlayer;
    private ArrayList<CardModel> chosenCards = new ArrayList<>();

    private int boardWidth;
    private int boardHeight;

    public static GameModel getInstance() {
        return instance;
    }

}
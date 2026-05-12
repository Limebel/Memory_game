package org.example.server;

import org.example.common.CardModel;
import org.example.common.GameModel;

public interface GameEventListener {
    void onInit(GameModel game);
    void onBoardStateChange(GameModel game);
    void onCardFlipped(GameModel game, int index);
    void onScoreChange(GameModel game);
    void onGameFinish(GameModel game);
}

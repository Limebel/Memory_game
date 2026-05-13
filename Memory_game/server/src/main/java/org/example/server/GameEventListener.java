package org.example.server;

import org.example.common.CardModel;
import org.example.common.GameModel;
import org.example.common.PlayerModel;

public interface GameEventListener {
    void onInit(GameModel game);
    void onBoardStateChange(GameModel game);
    void onCardFlipped(GameModel game, int index);
    void onScoreChange(GameModel game);
    void onGameFinish(GameModel game);
    void onSizeChoice(GameModel game);
    void onSendMessage(String message, PlayerModel player);
}

package org.example.server;

import org.example.common.GameModel;

public interface GameEventListener {
    void onInit(GameModel game);
    void onBoardStateChange(GameModel game);
    void onScoreChange(GameModel game);
}

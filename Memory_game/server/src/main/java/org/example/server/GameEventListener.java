package org.example.server;

import org.example.common.GameModel;

public interface GameEventListener {
    void onInit(GameModel game);
    void onStateChanged(GameModel game);
}

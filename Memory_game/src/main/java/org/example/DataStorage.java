package org.example;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class DataStorage {
    private static final DataStorage instance = new DataStorage();

    private String thisNickname;
    private String opponentNickname;

    private int boardWidth;
    private int boardHeight;

    public static DataStorage getInstance() {
        return instance;
    }
}
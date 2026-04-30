package org.example.server;

import lombok.Getter;
import org.example.common.CardModel;
import org.example.common.GameModel;
import org.example.common.GameState;
import org.example.common.PlayerModel;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// Stub for actual game logic
@Getter
public class GameController {
    private GameModel game = new GameModel();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void handleConnect(PlayerModel player){
        if(game.getPlayers().get(0)==null){
            game.getPlayers().set(0, player);
        }
        else if(game.getPlayers().get(1)==null){
            game.getPlayers().set(1, player);
        }

        if (game.getPlayers().get(0) != null && game.getPlayers().get(1) != null) {
            startGame();
        }
    }

    public void startGame(){
        game.setBoardHeight(5);
        game.setBoardWidth(6);
        initializeCards();
        game.setCurrentPlayer(0);
        game.setState(GameState.FIRST_CARD);
    }

    private void initializeCards(){

        //for(int i;i<)
    }

    public boolean handleFlip(PlayerModel player, int index) {
        if (!player.equals(game.getPlayers().get(game.getCurrentPlayer()))){
            return false;
        }
        GameState currentState = game.getState();
        if (currentState != GameState.FIRST_CARD && currentState != GameState.SECOND_CARD){
            return false;
        }
        // here should be also check for index out of range if index value is chosen by player
        CardModel card = game.getCards().get(index);
        if (card.getIfFlipped() || card.getIfMatched()){
            return false;
        }

        card.setIfFlipped(true);
        game.getChosenCards().add(card);
        System.out.println(player + "flipped card" + card);

        if(currentState == GameState.FIRST_CARD){
            game.setState(GameState.SECOND_CARD);
        }
        else{
            game.setState(GameState.CHECKING);
            handleChecking(player);
            handleTurnEnd();
        }
        return true;
    }

    private void handleTurnEnd(){
        game.setCurrentPlayer((game.getCurrentPlayer()+1)%2);
        game.setState(GameState.FIRST_CARD);
        game.getChosenCards().clear();
    }

    private void handleChecking(PlayerModel player){
        CardModel card1 = game.getChosenCards().get(0);
        CardModel card2 = game.getChosenCards().get(1);
        if (card1.getValue().equals(card2.getValue())){
            card1.setIfMatched(true);
            card2.setIfMatched(true);
            player.setScore(player.getScore()+1);
            PlayerModel player1 = game.getPlayers().get(0);
            PlayerModel player2 = game.getPlayers().get(1);
            if(player1.getScore()+player2.getScore() == (game.getCards().size()/2)){
                game.setState(GameState.GAME_FINISHED);
                //handleFinish();
            }
        }
        else{
            card1.setIfFlipped(false);
            card2.setIfFlipped(false);
        }
    }

/*    public void processMove(Card card) {
         // TODO: implement match checking, moves, etc.
    }*/

    public Icon getCardFront(int index) {
        return UIManager.getIcon("OptionPane.informationIcon"); // placeholder
    }

    public Icon getCardBack() {
        return UIManager.getIcon("OptionPane.warningIcon"); // placeholder
    }
}
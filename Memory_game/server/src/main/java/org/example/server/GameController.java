package org.example.server;

import lombok.Getter;
import lombok.Setter;
import org.example.common.CardModel;
import org.example.common.GameModel;
import org.example.common.GameState;
import org.example.common.PlayerModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class representing game logic
 */
@Setter
@Getter
public class GameController {
    //game representation
    private GameModel game;
    //helper field indicating how many players have sent ready message
    private int readyPlayers = 0;
    //used to delay action of flipping cards back
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    //listener that forwards game controller messages to game server
    //It is set initialized inside GameServer class
    private GameEventListener listener;
    private ClientHandler channel;

    /**
     * Contructor for game controller
     * It sets game state into 'Waiting For Player'
     */
    public GameController(){
        game = new GameModel();
        game.setState(GameState.WAITING_FOR_PLAYERS);
    }

    /**
     * Method for assigning connected players to game
     * If the second player connects game starts
     * @param player player that is connecting to game
     */
    public synchronized int handleConnect(PlayerModel player){
        int index = 2;
        if(game.getPlayers().size()<2){
            game.getPlayers().add(0, null);
            game.getPlayers().add(0, null);
        }
        if(game.getPlayers().get(0)==null){
            game.getPlayers().set(0, player);
            index = 0;
        }
        else if(game.getPlayers().get(1)==null){
            game.getPlayers().set(1, player);
            index = 1;
        }

        if (game.getPlayers().get(0) != null && game.getPlayers().get(1) != null) {
            channel.send("SIZE CHOICE");
            //startGame();
        }
        return index;
    }

    /**
     * Method for initializing game state
     * At the end client are notified to change their GUI
     */
    public synchronized void startGame(int width, int height){
        //Boards size is fixed for now

        //TODO: setting flexible board size by one of players
        game.setBoardHeight(height);
        game.setBoardWidth(width);
        initializeCards();
        game.setCurrentPlayer(0);

        //Change of game state to FIRST CARD is handled inside handleReady method

        notifyInit();
    }

    /**
     * Method for initialization of cards on the board.
     * It creates appropriate number of unflipped and unmatched cards
     * and shuffles them so the position of the cards is random
     */
    private synchronized void initializeCards(){
        int rows = game.getBoardHeight();
        int cols = game.getBoardWidth();
        ArrayList<CardModel> cards = new ArrayList<>();
        for (int i=0; i<rows*cols; i++){
            CardModel card = new CardModel();
            card.setValue(i/2);
            card.setIfFlipped(false);
            card.setIfMatched(false);
            cards.add(card);
        }
        Collections.shuffle(cards);
        game.setCards(cards);
    }

    /**
     * Method for simulating flipping of a card.
     * If it is turn of a player to flip a card, it is flipped
     * If this is the second card game proceeds to checking if the flipped cards are similar
     * @param player represent player who sent flip command
     * @param index represent index in the card table to be flipped
     * @return true if the player managed to flip the card
     */
    public synchronized boolean handleFlip(PlayerModel player, int index) {
        if (!player.equals(game.getPlayers().get(game.getCurrentPlayer()))){
            System.out.println("Wrong player tried to flip the card");
            return false;
        }
        GameState currentState = game.getState();
        if (currentState != GameState.FIRST_CARD && currentState != GameState.SECOND_CARD){
            System.out.println("Player tried to flipped card when it's not turn for it");
            return false;
        }
        if (index < 0 || index >= game.getBoardHeight()* game.getBoardWidth()){
            System.out.println("Invalid index of flipped card");
            return false;
        }
        CardModel card = game.getCards().get(index);
        if (card.getIfFlipped() || card.getIfMatched()){
            System.out.println("Player tried to flipped card, which is already matched or flipped");
            return false;
        }

        card.setIfFlipped(true);
        //send updated state of the board to clients
        notifyCardFlipped(index);
        game.getChosenCards().add(card);
        System.out.println(player + "flipped card" + card);

        if(currentState == GameState.FIRST_CARD){
            game.setState(GameState.SECOND_CARD);
        }
        else{
            game.setState(GameState.CHECKING);
            handleChecking(player);
        }
        return true;
    }

    /**
     * Method for checking if the chosen cards are similar
     * If cards match, they are set to matched state
     * and player gets one point.
     * Additionally, if players matched all cards the game proceeds
     * to finished state.
     * If cards do not match they are flipped back
     * @param player The player that flipped the card
     */
    private synchronized void handleChecking(PlayerModel player){
        //TODO:add delay for matched cards
        CardModel card1 = game.getChosenCards().get(0);
        CardModel card2 = game.getChosenCards().get(1);
        if (card1.getValue().equals(card2.getValue())){
            card1.setIfMatched(true);
            card2.setIfMatched(true);
            card1.setIfFlipped(false);
            card2.setIfFlipped(false);
            notifyBoardStateUpdate();
            player.setScore(player.getScore()+1);
            System.out.println(player.getName() + "'s score is now " + player.getScore());

            //send information about score to clients
            notifyScoreUpdate();

            PlayerModel player1 = game.getPlayers().get(0);
            PlayerModel player2 = game.getPlayers().get(1);
            if(player1.getScore()+player2.getScore() == (game.getCards().size()/2)){
                System.out.println("Finish!!!");
                game.setState(GameState.GAME_FINISHED);
                // TODO:handleFinish();
            }
            else{
                game.setState(GameState.FIRST_CARD);
                game.getChosenCards().clear();
            }
        }
        else{
            //Cards are flipped again after 2 seconds and we proceed to the next turn
            scheduler.schedule(() -> {
                synchronized (this) {
                    List<Integer> indexes = getFirstTwoFlippedIndexes();
                    System.out.println("Flip back: "+indexes.toString());
                    notifyCardFlipped(indexes.get(0));
                    notifyCardFlipped(indexes.get(1));
                    card1.setIfFlipped(false);
                    card2.setIfFlipped(false);
                    handleTurnEnd();
                }
            }, 2, TimeUnit.SECONDS);
        }
    }

    public List<Integer> getFirstTwoFlippedIndexes() {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < game.getCards().size(); i++) {
            CardModel card = game.getCards().get(i);

            if (Boolean.TRUE.equals(card.getIfFlipped())) {
                result.add(i);

                if (result.size() == 2) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Method for handling end of a turn
     * It changes current Player, changes game state
     * and clears pool of choosen cards
     */
    private synchronized void handleTurnEnd(){
        game.setCurrentPlayer((game.getCurrentPlayer()+1)%2);
        game.setState(GameState.FIRST_CARD);
        game.getChosenCards().clear();
    }

    /**
     * Method to activate onInit message exchange
     */
    private synchronized void notifyInit() {
        if (listener != null) {
            listener.onInit(game);
        }
    }

    /**
     * Method to activate board state update sending
     */
    private synchronized void notifyBoardStateUpdate() {
        if (listener != null) {
            listener.onBoardStateChange(game);
        }
    }

    /**
     * Method to activate score update sending
     */
    private synchronized void notifyScoreUpdate() {
        if (listener != null) {
            listener.onScoreChange(game);
        }
    }

    private synchronized void notifyCardFlipped(int index){
        if (listener != null){
            listener.onCardFlipped(game, index);
        }
    }

    /**
     *Method for handling players delay at the start of the game
     *It initializes state to FIRST CARD only if both players are declared ready
     */
    public synchronized void handleReady() {
        readyPlayers++;
        if (readyPlayers == 2) {
            game.setState(GameState.FIRST_CARD);
            notifyBoardStateUpdate();
        }
    }
}
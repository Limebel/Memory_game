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
import java.util.concurrent.ScheduledFuture;
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

    //task that is set for player disconnection
    //It is needed to be an object in case player reconnects
    private ScheduledFuture<?> disconnectTask;
    //Game state before reconnection happened
    //Needed to properly reconnect
    private GameState previousState;

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
    public synchronized void handleConnect(PlayerModel player){
        // reconnect case
        //TODO: check if reconnection works
        for (PlayerModel p : game.getPlayers()) {
            if (p != null && p.getName().equals(player.getName())) {
                //should trigger only if player is disconnected
                if(!p.isConnected()) {
                    p.setConnected(true);
                    System.out.println("Player reconnected");
                    notifyOnSendMessage("Player Reconnected", player);
                    if (disconnectTask != null) {
                        disconnectTask.cancel(false);
                    }
                    notifyOnReconnection(player);
                    if (game.getPlayers().stream().allMatch(PlayerModel::isConnected)) {
                        game.setState(previousState);
                    }
                    return;
                }
            }
        }
        if(game.getPlayers().size()<2){
            game.getPlayers().add(player);
            notifyOnConnect(game, game.getPlayers().size()-1);
            if (game.getPlayers().size()==2) {
                notifyOnSizeChoice();
            }
        }
        else{
            notifyOnSendMessage("Game is full", player);
        }
    }

    /**
     * Method for initializing game state
     * At the end client are notified to change their GUI
     */
    public synchronized void startGame(int width, int height){
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
            notifyOnSendMessage("Invalid flip: not your turn", player);
            System.out.println("Wrong player tried to flip the card");
            return false;
        }
        GameState currentState = game.getState();
        if (currentState != GameState.FIRST_CARD && currentState != GameState.SECOND_CARD){
            notifyOnSendMessage("Invalid flip: not flipping phase", player);
            System.out.println("Player tried to flipped card when it's not turn for it");
            return false;
        }
        if (index < 0 || index >= game.getBoardHeight()* game.getBoardWidth()){
            System.out.println("Invalid index of flipped card");
            return false;
        }
        CardModel card = game.getCards().get(index);
        if (card.getIfFlipped() || card.getIfMatched()){
            notifyOnSendMessage("Invalid flip: card is already matched or flipped", player);
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
            handleChecking();
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
     */
    private synchronized void handleChecking(){
        CardModel card1 = game.getChosenCards().get(0);
        CardModel card2 = game.getChosenCards().get(1);
        if (card1.getValue().equals(card2.getValue())){
            PlayerModel player = game.getPlayers().get(game.getCurrentPlayer());
            player.setScore(player.getScore()+1);
            System.out.println(player.getName() + "'s score is now " + player.getScore());
            //send information about score to clients
            notifyScoreUpdate();

            //Cards disappear after 1 second
            scheduler.schedule(() -> {
                synchronized (this) {
                    card1.setIfMatched(true);
                    card2.setIfMatched(true);
                    card1.setIfFlipped(false);
                    card2.setIfFlipped(false);
                    notifyBoardStateUpdate();
                }
            }, 1, TimeUnit.SECONDS);
            PlayerModel player1 = game.getPlayers().get(0);
            PlayerModel player2 = game.getPlayers().get(1);
            if(player1.getScore()+player2.getScore() == (game.getCards().size()/2)){
                System.out.println("Finish!!!");
                game.setState(GameState.GAME_FINISHED);
                handleFinish();

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

    /**
     * Method for handling end of the game events
     */
    public synchronized void handleFinish(){
        notifyWinner();
    }

    /**
     * Method return first two cards that are flipped
     * (meaning the all that are flipped - were chosen to flipped in this turn)
     * It is used to organize board after checking for cards matching
     * @return 2 elements list of cards that are flipped
     */
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
     * Method for handling player being disconnected
     * If player do not connect back in 5 seconds the game is finished
     * and other player is decided to be the winner
     * @param player the player being disconnected
     */
    public synchronized void handleDisconnect(PlayerModel player){
        player.setConnected(false);
        if(game.getState()!=GameState.PAUSED){previousState = game.getState();}
        game.setState(GameState.PAUSED);
        notifyOnPlayerDisconnected(player);

        disconnectTask = scheduler.schedule(() -> {
            synchronized(this){
                if(!player.isConnected()){
                    System.out.println("Reconnect timeout");
                    game.setState(GameState.GAME_FINISHED);
                    handleFinish();
                }
            }
        }, 30, TimeUnit.SECONDS); //30 seconds for testing
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
     * Method to activate board state update message
     */
    private synchronized void notifyBoardStateUpdate() {
        if (listener != null) {
            listener.onBoardStateChange(game);
        }
    }

    /**
     * Method to activate score update message
     */
    private synchronized void notifyScoreUpdate() {
        if (listener != null) {
            listener.onScoreChange(game);
        }
    }

    /**
     * Method to activate card being flipped message
     */
    private synchronized void notifyCardFlipped(int index){
        if (listener != null){
            listener.onCardFlipped(game, index);
        }
    }

    /**
     * Method to activate score winner notification message
     */
    private synchronized void notifyWinner(){
        if (listener != null){
            listener.onGameFinish(game);
        }
    }

    /**
     * Method to activate size choice message
     */
    private synchronized void notifyOnSizeChoice(){
        if (listener != null){
            //player index is fixed set on 1 meaning second player will always choose size
            listener.onSizeChoice(game);
        }
    }

    /**
     * Method to activate custom message
     */
    private synchronized void notifyOnSendMessage(String message,PlayerModel player){
        if (listener != null){
            listener.onSendMessage(message, player);
        }
    }

    /**
     * Method to inform server about player disconnection
     */
    private synchronized void notifyOnPlayerDisconnected(PlayerModel player){
        if (listener != null){
            listener.onPlayerDisconnected(player);
        }
    }

    /**
     * Method to inform server about player disconnection
     */
    private synchronized void notifyOnReconnection(PlayerModel player){
        if (listener != null){
            listener.onReconnection(game, player);
        }
    }

    /**
     * Method to meesage player index
     */
    private synchronized void notifyOnConnect(GameModel game, int index){
        if (listener != null){
            listener.onConnect(game, index);
        }
    }

    /**
     *Method for handling players delay at the start of the game
     *It initializes state to FIRST CARD only if both players are declared ready
     */
    public synchronized void handleReady() {
        if(game.getState()==GameState.WAITING_FOR_PLAYERS) {
            readyPlayers++;
            if (readyPlayers == 2) {
                game.setState(GameState.FIRST_CARD);
                notifyBoardStateUpdate();
            }
        }
    }
}
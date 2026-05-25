package org.example.server;

import org.example.common.GameModel;
import org.example.common.GameState;
import org.example.common.PlayerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing instance of game server.
 * It is responsible for creating connection with clients,
 * holding information about them
 * and creating messages with information about game states
 */
public class GameServer {
    //server port
    private final int port;
    //game controller
    private final GameController controller;
    //list of connected clients
    private List<ClientHandler> clients = new ArrayList<>();

    /**
     * Constructor of game server.
     * Set listener for game server, which task is to inform
     * game server about messages that game controller needs to send
     * to a client.
     * @param port port on which game server is created and listenes for clients.
     */
    public GameServer(int port) {
        this.port = port;
        this.controller = new GameController();

        //setting listener for game controller
        controller.setListener(new GameEventListener() {
            @Override
            public void onInit(GameModel game) {
                sendToAll(initMessage(game));
                sendToAll(namesMessage(game));
            }

            @Override
            public void onBoardStateChange(GameModel game) {sendToAll(boardStateMessage(game));}

            @Override
            public void onCardFlipped(GameModel game, int index){sendToAll(cardFlippedMessage(game, index));}

            @Override
            public void onScoreChange(GameModel game){sendToAll(scoreMessage(game));}

            @Override
            public void onGameFinish(GameModel game){ broadcastFinishGame(game);}

            /**
             * Second player is informed to chose board size
             * @param game the game that is played, provided by game controller
             */
            @Override
            public void onSizeChoice(GameModel game){
                //second player always chooses board size
                PlayerModel secondPlayer = game.getPlayers().get(1);
                for (ClientHandler c : clients) {
                    if (c.getPlayer().equals(secondPlayer)) {
                        c.send("SIZE CHOICE");
                        return;
                    }
                }
                throw new IllegalStateException("No second player found");
            }

            @Override
            public void onSendMessage(String message, PlayerModel player){
                for (ClientHandler c : clients) {
                    if (c.getPlayer() == player) {
                        c.send("CUSTOM MESSAGE:" + message);
                        return;
                    }
                }
                throw new IllegalStateException("No player to send message found");
            }

            @Override
            public void onPlayerDisconnected (PlayerModel player){
                Iterator<ClientHandler> it = clients.iterator();
                while (it.hasNext()) {
                    ClientHandler c = it.next();
                    if (c.getPlayer() == player) {
                        it.remove();
                        System.out.println("Removed disconnected client: "
                                        + player.getName());
                        return;
                    }
                }
                throw new IllegalStateException("No client found to remove");
            }

            @Override
            public void onReconnection (GameModel game, PlayerModel player){
                for (ClientHandler c : clients) {
                    if (c.getPlayer() == player) {
                        int index=0;
                        for(PlayerModel gamePlayer : game.getPlayers()){
                            if (gamePlayer.equals(player)){
                                c.send("Player " + index + " reconnected");
                            }
                            index++;
                        }
                        sendStateAfterReconnection(game, c);
                        return;
                    }
                }
                throw new IllegalStateException("No player to send message found");
            }

            @Override
            public void onConnect (GameModel game,int index){
                for (ClientHandler c : clients) {
                    if (c.getPlayer() == game.getPlayers().get(index)) {
                        c.send("Player " + index + " connected");
                        return;
                    }
                }
                throw new IllegalStateException("No player to send message found");
            }
        });
    }

    /**
     * Method that starts server and waits in infinite loop
     * for clients to connect on the server port.
     * If client connects then it is added to client list
     * and handled in HandleClient Thread
     * @throws IOException handled in the main function
     */
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            //If there are already 2 clients connected no new client can connect
            ClientHandler handler = new ClientHandler(clientSocket, controller);
            clients.add(handler);
            new Thread(handler).start();
        }
    }

    /**
     * Method for sending initial state of the board to the client
     * Message structure is like:
     * INIT:BoardHeight:BoardWidth:valueCard1,valueCard2...
     * @param game the game that is played, provided by game controller
     * @return initialization message
     */
    private String initMessage(GameModel game) {
        StringBuilder sb = new StringBuilder("INIT:");
        sb.append(game.getBoardHeight()).append(":");
        sb.append(game.getBoardWidth()).append(":");

        for (int i = 0; i < game.getCards().size(); i++) {
            sb.append(game.getCards().get(i).getValue());
            if (i < game.getCards().size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    /**
     * Method for sending updated state of flipped cards
     * Message structure is like:
     * STATE:true,false,true...
     * @param game the game that is played, provided by game controller
     */
    private String boardStateMessage(GameModel game) {
        StringBuilder sb = new StringBuilder("STATE:");
        sb.append(game.getBoardHeight()).append(":");
        sb.append(game.getBoardWidth()).append(":");

        for (int i = 0; i < game.getCards().size(); i++) {
            sb.append(game.getCards().get(i).getIfMatched());
            if (i < game.getCards().size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    private String cardFlippedMessage(GameModel game, Integer index) {
        StringBuilder sb = new StringBuilder("FLIPPED:");
        sb.append(index.toString());

        return sb.toString();
    }

    /**
     * Method for sending updated state of score
     * Message structure is like:
     * SCORE:4,5
     * @param game the game that is played, provided by game controller
     */
    private String scoreMessage(GameModel game) {
        StringBuilder sb = new StringBuilder("SCORE:");
        sb.append(game.getPlayers().get(0).getScore()).append(":");
        sb.append(game.getPlayers().get(1).getScore());

        return sb.toString();
    }

    /**
     * Broadcasting player names
     * @param game the game that is played, provided by game controller
     */
    private String namesMessage(GameModel game){
        StringBuilder sb = new StringBuilder("NAMES:");
        sb.append(game.getPlayers().get(0).getName()).append(":");
        sb.append(game.getPlayers().get(1).getName());
        return sb.toString();
    }

    /**
     * Broadcasting information about game finish and the winner of the game
     * If game ended by player disconnection other player is decided the winner
     * @param game the game that is played, provided by game controller
     */
    private void broadcastFinishGame(GameModel game){
        StringBuilder sb = new StringBuilder("WON:");
        if(!game.getPlayers().get(1).isConnected()){
            sb.append(0);
        }
        else if(!game.getPlayers().get(0).isConnected()){
            sb.append(1);
        }
        else if(game.getPlayers().get(0).getScore() > game.getPlayers().get(1).getScore()){
            sb.append(0);
        }
        else if (game.getPlayers().get(1).getScore() > game.getPlayers().get(0).getScore()){
            sb.append(1);
        }
        else{
            sb.append("TIE");
        }

        sendToAll(sb.toString());
        System.out.println("sent " + sb);
    }

    private void sendStateAfterReconnection(GameModel game, ClientHandler client){
        if(controller.getPreviousState()!= GameState.WAITING_FOR_PLAYERS) {
            client.send(initMessage(game));
            client.send(namesMessage(game));
            client.send(boardStateMessage(game));
            client.send(scoreMessage(game));
            for (int i = 0; i < game.getCards().size(); i++) {
                if (game.getCards().get(i).getIfFlipped()) {
                    client.send(cardFlippedMessage(game, i));
                }
            }
        }
        else{
            if(game.getPlayers().size()==2){
                controller.getListener().onSizeChoice(game);
            }
        }
    }

    /**
     * Method that sends provided message to all clients connected
     * @param msg message to be sent
     */
    private void sendToAll(String msg) {
        for (ClientHandler c : clients) {
            c.send(msg);
        }
    }
}

package org.example.server;

import org.example.common.GameModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
                broadcastInit(game);
            }

            @Override
            public void onBoardStateChange(GameModel game) {
                broadcastBoardState(game);
            }

            @Override
            public void onCardFlipped(GameModel game, int index){
                broadcastCardFlipped(game, index);
            }

            @Override
            public void onScoreChange(GameModel game){
                broadcastScoreChange(game);
            }

            @Override
            public void onGameFinish(GameModel game){ broadcastFinishGame(game);}

            @Override
            public void onSizeChoice(GameModel game, int playerIndex){

            }

            @Override
            public void onSendMessage(String message, int playerIndex){

            }
            //TODO:Other neccessary broadcasts
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
            if (clients.size() >= 2) {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Game is full already, cannot add another player");
                clientSocket.close();
                continue;
            }
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
     */
    private void broadcastInit(GameModel game) {
        StringBuilder sb = new StringBuilder("INIT:");
        sb.append(game.getBoardHeight()).append(":");
        sb.append(game.getBoardWidth()).append(":");

        for (int i = 0; i < game.getCards().size(); i++) {
            sb.append(game.getCards().get(i).getValue());
            if (i < game.getCards().size() - 1) sb.append(",");
        }
        sendToAll(sb.toString());
        broadcastNames(game);
    }

    /**
     * Method for sending updated state of flipped cards
     * Message structure is like:
     * STATE:true,false,true...
     * @param game the game that is played, provided by game controller
     */
    private void broadcastBoardState(GameModel game) {
        StringBuilder sb = new StringBuilder("STATE:");
        sb.append(game.getBoardHeight()).append(":");
        sb.append(game.getBoardWidth()).append(":");

        for (int i = 0; i < game.getCards().size(); i++) {
            sb.append(game.getCards().get(i).getIfMatched());
            if (i < game.getCards().size() - 1) sb.append(",");
        }
        sendToAll(sb.toString());
    }

    private void broadcastCardFlipped(GameModel game, Integer index) {
        StringBuilder sb = new StringBuilder("FLIPPED:");
        sb.append(index.toString());

        sendToAll(sb.toString());
    }

    /**
     * Method for sending updated state of score
     * Message structure is like:
     * SCORE:4,5
     * @param game the game that is played, provided by game controller
     */
    private void broadcastScoreChange(GameModel game) {
        StringBuilder sb = new StringBuilder("SCORE:");
        sb.append(game.getPlayers().get(0).getScore()).append(":");
        sb.append(game.getPlayers().get(1).getScore());

        sendToAll(sb.toString());
    }

    private void broadcastNames(GameModel game){
        StringBuilder sb = new StringBuilder("NAMES:");
        sb.append(game.getPlayers().get(0).getName()).append(":");
        sb.append(game.getPlayers().get(1).getName());

        sendToAll(sb.toString());
    }

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

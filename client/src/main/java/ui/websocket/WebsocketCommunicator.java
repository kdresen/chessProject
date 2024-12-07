package ui.websocket;

import javax.websocket.*;
import java.io.IOException;

import chess.ChessGame;
import com.google.gson.Gson;

import exception.ResponseException;
import ui.DrawChessBoard;
import ui.Client;
import websocket.commands.JoinPlayer;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

public class WebsocketCommunicator extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebsocketCommunicator(String url, ServerMessageHandler serverMessageHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    NotificationMessage notif = new Gson().fromJson(message, NotificationMessage.class);
                    ServerMessageHandler.notify(notif);
                    //handleMessage(message);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        System.out.println("opened websocket connection");
    }


//    public void handleConnect(String playerName) throws ResponseException {
//        try {
//            JoinPlayer = new JoinPlayer(session, playerName);
//        }
//    }

    private void handleMessage(String message) {
        if (message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
            printNotification(notificationMessage.getMessage());
        }
        else if (message.contains("\"serverMessageType\":\"ERROR\"")) {
            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
            printNotification(error.getErrorMessage());
        }
        else if (message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
            // TODO print out the game

        }
    }

    private void printNotification(String message) {
        System.out.print(ERASE_LINE + '\r');
        System.out.printf("\n%s\n[IN-GAME] >>> ", message);
    }

    private void printLoadedGame(ChessGame game, ChessGame.TeamColor teamColor) {
        System.out.print(ERASE_LINE + "\r\n");
        DrawChessBoard.createChessBoard(game, teamColor);
        System.out.print("[IN-GAME] >>> ");
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }



}

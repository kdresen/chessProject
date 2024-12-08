package ui.websocket;

import javax.websocket.*;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.GameClient;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.net.URI;

@ClientEndpoint
public class WebsocketCommunicator {

    Session session;
    private final Gson gson = new Gson();
    private ChessGame game;
    public ChessGame.TeamColor teamColor = ChessGame.TeamColor.WHITE;
    private final GameClient gameClient;

    public WebsocketCommunicator(String url, GameClient gameClient) throws Exception {
        this.gameClient = gameClient;
        connect(url);
    }

    private void connect(String url) throws Exception {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(url));
            System.out.println("Connected to websocket server at: " + url);
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        if (message.contains("NOTIFICATION")) {
            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
            handleNotification(notificationMessage.getMessage());
        }
        else if (message.contains("ERROR")) {
            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
            handleError(error.getErrorMessage());
        }
        else if (message.contains("LOAD_GAME")) {
            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
            handleLoadGame(loadGame.getGame());
        }
        else {
            System.err.println("Unrecognized message type: " + message);
        }
    }
    public void sendMessage(UserGameCommand command) {
        try {
            if (session != null && session.isOpen()) {
                String json = gson.toJson(command);
                session.getBasicRemote().sendText(json);
            } else {
                System.err.println("Websocket connection is closed, cannot send message");
            }
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to close websocket session: " + e.getMessage());
        }
    }

    private void handleLoadGame(ChessGame game) {
        gameClient.loadGame(game);
    }

    private void handleNotification(String message) {
        System.out.println("\rNotification: " + message);
        System.out.print("[IN_GAME] >>> ");
    }

    private void handleError(String errorMessage) {
        System.out.println("Error from server: " + errorMessage);
    }



}

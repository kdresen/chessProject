package server.websocket;

import com.google.gson.Gson;
import dataaccess.daointerfaces.*;
import dataaccess.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.io.ConnectionManager;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Timer;

import static server.Server.gameSessions;

@WebSocket
public class WebSocketHandler {
//    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        gameSessions.put(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        gameSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.printf("Received: %s\n", message);

        // connect to game with connect command
        if (message.contains("\"commandType\":\"CONNECT\"")) {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            gameSessions.replace(session, command.getGameID());
            handleJoin(session, command);
        }

        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> loadGame(session, message);
            case NOTIFICATION -> notification(session, message);
            case ERROR -> sendError(session, message);
        }
    }

    private void handleJoin(Session session, UserGameCommand command) throws IOException {

        try {

        }
    }

    private void loadGame(Session session, String message) throws IOException {

    }

    private void notification(Session session, String message) throws IOException {

    }

    private void sendError(Session session, String message) throws IOException {

    }
}

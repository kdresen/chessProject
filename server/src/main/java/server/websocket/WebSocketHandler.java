package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.daointerfaces.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import server.Server;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import static server.Server.gameSessions;

@WebSocket
public class WebSocketHandler {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private static final Map<Integer, List<Session>> CONNECTIONS = new HashMap<>();

    public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) throws Exception {
        System.out.println("WebSocket connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("New message: " + message + "\n");

        if (message.contains("MAKE_MOVE")) {
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            try {
                handleMakeMove(session, command);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            return;
        }
        UserGameCommand command = parseCommand(message);
        try {
            handleCommand(command, session);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private UserGameCommand parseCommand(String message) {
        return new Gson().fromJson(message, UserGameCommand.class);
    }

    private void handleCommand(UserGameCommand command, Session session) throws DataAccessException {
        // handle CONNECT, MAKE_MOVE, LEAVE, and RESIGN commands
        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(session, command);
                break;
            case LEAVE:
                handleLeave(session, command);
                break;
            case RESIGN:
                handleResign(session, command);
                break;
            default:
                System.out.println("Unknown command type: " + command.getCommandType());
                break;

        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws DataAccessException {
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = authDAO.getAuthData(authToken).username();
        GameData gameData = gameDAO.getGameByID(gameID);

        if (Objects.equals(authToken, null)) {
            System.out.println("User not found.");
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        if (gameData == null) {
            System.out.println("Game not found.");
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }

        CONNECTIONS.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);

        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        }
        else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        NotificationMessage notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has joined the game as " + color);
        String json = new Gson().toJson(notif);
        broadcastMessageExclude(json, gameID, session);

        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
        String jsonGame = new Gson().toJson(gameMessage);
        sendMessage(jsonGame, session);
    }

    private void handleMakeMove(Session session, MakeMoveCommand command)
            throws DataAccessException {
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        GameData gameData = gameDAO.getGameByID(gameID);
        ChessMove move = command.getMove();

        if (Objects.equals(authToken, null)) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        if (!(authDAO.getAuthData(authToken).username().equals(gameData.blackUsername()) && gameData.game().getTeamTurn() == ChessGame.TeamColor.BLACK)
                && !(authDAO.getAuthData(authToken).username().equals(gameData.whiteUsername()) && gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE)) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "wrong turn");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        if (gameData.gameName() == null) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        if (gameData.game().getGameOver()) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        try {

        }
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {

    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket closed: " + statusCode + " " + reason);
    }

    private void broadcastMessage(String message, Integer gameID) {
        ConcurrentHashMap<String, Connection> sessions = CONNECTIONS.
    }


    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return null;
        }
    }
}

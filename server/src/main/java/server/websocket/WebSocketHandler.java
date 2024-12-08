package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.daointerfaces.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.*;

@WebSocket
public class WebSocketHandler {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private static final Map<Integer, List<Session>> CONNECTIONS = new HashMap<>();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
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
        System.out.print("[IN_GAME] >>> ");
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
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = game.getTeamTurn();
            String username = authDAO.getAuthData(authToken).username();
            ChessGame.TeamColor otherTeam = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            String otherUsername = (otherTeam == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            game.makeMove(move);
            gameDAO.updateGame(game, gameID);
            LoadGameMessage messageLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            String json = new Gson().toJson(messageLoad);
            broadcastMessage(json, gameID);

            NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, color +
                    " user " + username + " moved from " + formatPosition(move.getStartPosition()) + " to "
            + formatPosition(move.getEndPosition()));
            json = new Gson().toJson(message);
            broadcastMessageExclude(json, gameID, session);
            if (game.isInCheckmate(otherTeam)) {
                message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, otherTeam +
                        " user " + otherUsername + " is in checkmate, " + color + " user " + username +
                        " wins");
                json = new Gson().toJson(message);
                broadcastMessage(json, gameID);
                game.setGameOver(true);
            }
            else if (game.isInCheck(otherTeam)) {
                message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, otherTeam +
                        " user " + otherUsername + " is in check");
                json = new Gson().toJson(message);
                broadcastMessage(json, gameID);
            }
            else if (game.isInStalemate(otherTeam) || game.isInStalemate(color)) {
                message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "game ends in stalemate");
                json = new Gson().toJson(message);
                broadcastMessage(json, gameID);
                game.setGameOver(true);
            }
            System.out.print("[IN_GAME] >>> ");
        } catch (InvalidMoveException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid move");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
        }
    }

    private String formatPosition(ChessPosition position) {
        char col = (char) ('a' + position.getColumn() - 1);
        return col + String.valueOf(position.getRow());
    }

    private void handleLeave(Session session, UserGameCommand command) throws DataAccessException {
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = authDAO.getAuthData(authToken).username();
        GameData gameData = gameDAO.getGameByID(gameID);

        if (Objects.equals(authToken, null)) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        if (gameData == null) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        }
        else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        NotificationMessage notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                color + " user " + username + " left the game");
        String json = new Gson().toJson(notif);
        broadcastMessageExclude(json, gameID, session);
        if (!color.equals("observer")) {
            ChessGame.TeamColor team = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            if (team == ChessGame.TeamColor.WHITE) {
                gameData = gameData.replaceWhiteUsername(null);
            }
            else {
                gameData = gameData.replaceBlackUsername(null);
            }
            gameDAO.updateGame(gameData.game(), gameID);
        }
        CONNECTIONS.get(gameID).remove(session);
        System.out.print("[IN_GAME] >>> ");
    }

    private void handleResign(Session session, UserGameCommand command) throws DataAccessException {
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = authDAO.getAuthData(authToken).username();
        GameData gameData = gameDAO.getGameByID(gameID);

        if (!username.equals(gameData.blackUsername())
                && !username.equals(gameData.whiteUsername())) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "wrong turn");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
            return;
        }
        if (Objects.equals(authToken, null)) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
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
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game over");
            String json = new Gson().toJson(error);
            sendMessage(json, session);
        } else {
            ChessGame game = gameData.game();
            NotificationMessage notif = getNotificationMessage(username, gameData);
            String json = new Gson().toJson(notif);
            broadcastMessage(json, gameID);
            game.setGameOver(true);
            gameDAO.updateGame(game, gameID);
        }
        System.out.print("[IN_GAME] >>> ");
    }

    private static NotificationMessage getNotificationMessage(String username, GameData gameData) {
        ChessGame.TeamColor color = (username.equals(gameData.whiteUsername())) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor otherColor = (color == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String otherUsername = (otherColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
        return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, color +
                " user " + username + " has resigned, " + otherColor + " user " + otherUsername + " wins");
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
        List<Session> gameSessions = CONNECTIONS.get(gameID);
        if (gameSessions != null) {
            for (Session gameSession : gameSessions) {
                if (gameSession.isOpen()) {
                    sendMessage(message, gameSession);
                }
            }
        }
    }

    private void broadcastMessageExclude(String message, Integer gameID, Session session) {
        List<Session> gameSessions = CONNECTIONS.get(gameID);
        if (gameSessions != null) {
            for (Session gameSession : gameSessions) {
                if (gameSession != session && gameSession.isOpen()) {
                    sendMessage(message, gameSession);
                }
            }
        }
    }

    private void sendMessage(String message, Session session) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}

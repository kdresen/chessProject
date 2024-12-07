package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.daointerfaces.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

//import static server.Server.gameSessions;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager myConnections = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
//        gameSessions.put(session, 0);
        System.out.println("WebSocket connected: " + session);
        myConnections.addSession("test", session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.printf("Received: %s\n", message);

        // connect to game with connect command
        if (message.contains("\"commandType\":\"CONNECT\"")) {
            JoinPlayer command = new Gson().fromJson(message, JoinPlayer.class);
//            gameSessions.replace(session, command.getGameID());
            handleJoinPlayer(session, new JoinPlayer(command.getCommandType(), command.getAuthToken(), command.getGameID(), command.getTeamColor()));
        }
        else if (message.contains("\"commmandType\":\"MAKE_MOVE")) {
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            handleMakeMove(session, command);
        }
    }

    private void handleJoinPlayer(Session session, JoinPlayer command) throws IOException {

        try {
            AuthData auth = server.Server.userService.getAuthData(command.getAuthToken());
            GameData game = server.Server.gameService.getGameByID(command.getGameID());

            // TODO add functionality for observer if command.getTeamColor() is null

            ChessGame.TeamColor joiningColor = command.getTeamColor().toString().equalsIgnoreCase("white") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            boolean correctColor;
            if (joiningColor == ChessGame.TeamColor.WHITE) {
                correctColor = Objects.equals(game.whiteUsername(), auth.username());
            }
            else {
                correctColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!correctColor) {
                ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: attempting to join with wrong color");
                handleError(session, error);
                return;
            }

            NotificationMessage notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,auth.username() + " has joined the game as " + command.getTeamColor().toString());
            broadcastMessage(session, notif);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleMakeMove(Session session, MakeMoveCommand command) throws IOException {
        try {
            AuthData auth = server.Server.userService.getAuthData(command.getAuthToken());
            GameData game = server.Server.gameService.getGameByID(command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                handleError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: You are observing this game"));
                return;
            }

            if (game.game().getGameOver()) {
                handleError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game is over"));
                return;
            }

            if (game.game().getTeamTurn().equals(userColor)) {
                game.game().makeMove(command.getMove());

                NotificationMessage notif;
                ChessGame.TeamColor opponentColor = userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                if (game.game().isInCheckmate(opponentColor)) {
                    notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Checkmate! " + auth.username() + " wins!");
                    game.game().setGameOver(true);
                }
                else if (game.game().isInStalemate(opponentColor)) {
                    notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Stalemate caused by " + auth.username() + "'s move! It's a tie!");
                    game.game().setGameOver(true);
                }
                else if (game.game().isInCheck(opponentColor)) {
                    notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "A move has been made by " + auth.username() + ", " + opponentColor.toString() + "is now in check!");
                }
                else {
                    notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "A move has been made by " + auth.username());
                }
                broadcastMessage(session, notif);
            }
        } catch (DataAccessException | InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLeave(Session session, LeaveGameCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuthData(command.getAuthToken());

            NotificationMessage notif = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, auth.username() + " left the game");
            broadcastMessage(session, notif);

            session.close();
        } catch (DataAccessException e) {
            handleError(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Not authorized"));
        }
    }

    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
//        for (Session session : gameSessions.keySet()) {
//            boolean inAGame = gameSessions.get(session) != 0;
//            boolean sameGame = gameSessions.get(session).equals(gameSessions.get(currSession));
//            boolean isSelf = session == currSession;
//            if ((toSelf || !isSelf) && inAGame && sameGame) {
//                sendMessage(session, message);
//            }
//        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void handleError(Session session, ErrorMessage error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
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

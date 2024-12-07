package ui.server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import request.CreateGamesRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;
import ui.websocket.ServerMessageHandler;
import ui.websocket.WebsocketCommunicator;
import websocket.commands.JoinPlayer;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.*;
import java.net.*;


//

public class ServerFacade {
    private final String serverUrl;
    WebsocketCommunicator ws;
    String authToken;



    public ServerFacade(String url) {
        serverUrl = url;
    }

    private String getAuthToken() {
        return authToken;
    }

    private void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    // http.setRequestProperty for setting authorization

    public LoginResult loginUser(UserData userData) throws ResponseException
    {
        var path = "/session";
        return this.makeRequest("POST", path, new LoginRequest(userData.username(), userData.password()), LoginResult.class, null);
    }

    public RegisterResult registerUser(String username, String password, String email) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, new RegisterRequest(username, password, email), RegisterResult.class, null);
    }

    public void logoutUser(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public CreateGamesResult createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, new CreateGamesRequest(gameName), CreateGamesResult.class, authToken);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResult.class, authToken);

    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String authToken) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, new JoinGameRequest(playerColor, gameID), null, authToken);
    }

    public void sendCommand(UserGameCommand command) {
        String message = new Gson().toJson(command);
        ws.sendMessage(message);
    }

    public void joinPlayer(int gameID, String authToken) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void joinObserver(int gameID) {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void makeMove(int gameID, ChessMove move) {
        sendCommand(new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move));
    }

    public void leave(int gameID) {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void resign(int gameID) {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }



    public void clearDatabases() {
        try {
            var path = "/db";
            this.makeRequest("DELETE", path, null, null, null);

        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException
    {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (URISyntaxException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                 response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryGameDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGamesRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGamesResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService() {
        this.gameDAO = MemoryGameDAO.getInstance();
        this.authDAO = MemoryAuthDAO.getInstance();
    }

    public CreateGamesResult createGame(CreateGamesRequest request) throws DataAccessException {
        if (Objects.equals(request, null)) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (request.gameName() == null || request.authToken() == null || request.authToken().isEmpty() || request.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        AuthData authData = authDAO.getAuthData(request.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return new CreateGamesResult(gameDAO.createGame(request.gameName()));
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        if (Objects.equals(request, null)) {
            throw new DataAccessException("Error: bad request");
        }
        if (Objects.equals(request.playerColor(), null)) {
            throw new DataAccessException("Error: bad request");
        }
        if (request.authToken() == null || request.authToken().isEmpty()) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData authData = authDAO.getAuthData(request.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        GameData gameData = gameDAO.getGameByID(request.gameID());
        if (gameData == null) {
            throw new DataAccessException("Error: bad request");
        }
        gameDAO.insertUser(request.gameID(), authData.username(), request.playerColor());
        return new JoinGameResult();
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        if (Objects.equals(request, null)) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (request.authToken() == null || request.authToken().isEmpty()) {
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData authData = authDAO.getAuthData(request.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        List<GameData> result = gameDAO.getAllGames();
        return new ListGamesResult(result);
    }
}

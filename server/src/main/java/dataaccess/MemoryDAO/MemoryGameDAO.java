package dataaccess.MemoryDAO;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MemoryGameDAO implements GameDAO {
    private List<GameData> games;
    private int totalGames;
    private static MemoryGameDAO instance;

    private MemoryGameDAO() {
        this.games = new ArrayList<GameData>();
        this.totalGames = 0;
    }

    public static MemoryGameDAO getInstance() {
        if (instance == null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        totalGames++;
        gameData = gameData.replaceGameID(totalGames);
        games.add(gameData);
        return gameData.gameID();
    }

    @Override
    public GameData getGameByName(String gameName) throws DataAccessException {
        // cool optional object I just learned about
        Optional<GameData> game = games.stream().filter(g -> Objects.equals(g.gameName(), gameName)).findFirst();
        // return null if game is not found
        return game.orElse(null);
    }

    @Override
    public void insertUser(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException {
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                if (playerColor == ChessGame.TeamColor.BLACK) {
                    gameData.replaceBlackUsername(username);
                } else {
                    gameData.replaceWhiteUsername(username);
                }
            }
        }
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        games.removeIf(gameData -> gameData.gameID() == gameID);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        games.clear();
    }
}

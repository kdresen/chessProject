package dataaccess.memorydao;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.daointerfaces.GameDAO;
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
    public int createGame(String gameName) throws DataAccessException {
        totalGames++;
        GameData gameData = new GameData(totalGames, null, null, gameName, new ChessGame());
        games.add(gameData);
        return gameData.gameID();
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        // cool optional object I just learned about
        Optional<GameData> game = games.stream().filter(g -> Objects.equals(g.gameID(), gameID)).findFirst();
        // return null if game is not found
        return game.orElse(null);
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(games);
    }

    @Override
    public void insertUser(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            GameData gameData = games.get(i);
            if (gameData.gameID() == gameID) {
                if (playerColor == ChessGame.TeamColor.BLACK) {
                    if (gameData.blackUsername() != null) {
                        throw new DataAccessException("Error: already taken");
                    }
                    games.set(i, gameData.replaceBlackUsername(username));
                } else {
                    if (gameData.whiteUsername() != null) {
                        throw new DataAccessException("Error: already taken");
                    }
                    games.set(i, gameData.replaceWhiteUsername(username));

                }
                break;
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

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
    private final List<GameData> games;
    private int totalGames;

    private MemoryGameDAO() {
        this.games = new ArrayList<>();
        this.totalGames = 0;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        totalGames++;
        GameData gameData = new GameData(totalGames, null, null, gameName, new ChessGame());
        games.add(gameData);
        return gameData.gameID();
    }

    @Override
    public GameData getGameByID(int gameID) {
        // cool optional object I just learned about
        Optional<GameData> game = games.stream().filter(g -> Objects.equals(g.gameID(), gameID)).findFirst();
        // return null if game is not found
        return game.orElse(null);
    }

    @Override
    public List<GameData> getAllGames() {
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
    public void removeUser(int gameID, ChessGame.TeamColor playerColor) {
        for (int i = 0; i < games.size(); i++) {
            GameData gameData = games.get(i);
            if (gameData.gameID() == gameID) {
                if (playerColor == ChessGame.TeamColor.BLACK) {
                    games.set(i, gameData.replaceBlackUsername(null));
                } else {
                    games.set(i, gameData.replaceWhiteUsername(null));
                }
                break;
            }
        }
    }

    @Override
    public void updateGame(ChessGame game, Integer gameID) {
        for (int i = 0; i < games.size(); i++) {
            GameData gameData = games.get(i);
            if (gameData.gameID() == gameID) {
                games.set(i, gameData.replaceGame(game));
            }
        }
    }

    @Override
    public void deleteGame(int gameID) {
        games.removeIf(gameData -> gameData.gameID() == gameID);
    }

    @Override
    public void deleteAllGames() {
        games.clear();
    }
}

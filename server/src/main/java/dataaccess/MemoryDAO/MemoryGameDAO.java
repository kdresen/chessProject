package dataaccess.MemoryDAO;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void insertUser(int gameID, ChessGame.TeamColor playerColor) throws DataAccessException {

    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {

    }

    @Override
    public void deleteAllGames() throws DataAccessException {

    }
}

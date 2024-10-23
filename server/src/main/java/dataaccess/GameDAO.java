package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    // create a new game
    // returns the gameID
    int createGame(String gameName) throws DataAccessException;

    // retrieve a game by the id
    GameData getGameByID(int gameID) throws DataAccessException;

    // add a user to a game
    void insertUser(int gameID, ChessGame.TeamColor playerColor) throws DataAccessException;

    // delete game
    void deleteGame(int gameID) throws DataAccessException;

    // delete all games
    void deleteAllGames() throws DataAccessException;
}

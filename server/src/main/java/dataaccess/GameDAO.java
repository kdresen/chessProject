package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    // create a new game
    // returns the gameID
    int createGame(GameData gameData) throws DataAccessException;

    // retrieve a game by the id
    GameData getGameByName(String gameName) throws DataAccessException;

    // add a user to a game
    void insertUser(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException;

    // delete game
    void deleteGame(int gameID) throws DataAccessException;

    // delete all games
    void deleteAllGames() throws DataAccessException;
}

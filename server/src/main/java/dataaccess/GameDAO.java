package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    // create a new game
    // returns the gameID
    int createGame(String gameName) throws DataAccessException;

    // retrieve a game by the id
    GameData getGameByID(int gameID) throws DataAccessException;

    // retrieve all games in a list
    List<GameData> getAllGames() throws DataAccessException;
    // add a user to a game
    void insertUser(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException;

    // delete game
    void deleteGame(int gameID) throws DataAccessException;

    // delete all games
    void deleteAllGames() throws DataAccessException;
}

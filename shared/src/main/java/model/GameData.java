package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public String getGameName() {
        return gameName;
    }
    public String getWhiteUsername() {
        return whiteUsername;
    }
    public String getBlackUsername() {
        return blackUsername;
    }

    public GameData replaceWhiteUsername(String newWhiteUsername) {
        return new GameData(gameID, newWhiteUsername, blackUsername, gameName, game);
    }
    public GameData replaceBlackUsername(String newBlackUsername) {
        return new GameData(gameID, whiteUsername, newBlackUsername, gameName, game);
    }

    public GameData replaceGame(ChessGame newGame) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, newGame);
    }
    public String toString() {
        return new Gson().toJson(this);
    }
}

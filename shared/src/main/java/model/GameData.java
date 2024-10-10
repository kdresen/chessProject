package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    GameData replaceGameID(int newGameID) {
        return new GameData(newGameID, whiteUsername, blackUsername, gameName, game);
    }
    GameData replaceWhiteUsername(String newWhiteUsername) {
        return new GameData(gameID, newWhiteUsername, blackUsername, gameName, game);
    }
    GameData replaceBlackUsername(String newBlackUsername) {
        return new GameData(gameID, whiteUsername, newBlackUsername, gameName, game);
    }
    GameData replaceGameName(String newGameName) {
        return new GameData(gameID, whiteUsername, blackUsername, newGameName, game);
    }
    GameData replaceGame(ChessGame newGame) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, newGame);
    }
}

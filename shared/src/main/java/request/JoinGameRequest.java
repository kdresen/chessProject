package request;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID, String authToken) {
    public JoinGameRequest replaceAuthToken(String newAuthToken) {
        return new JoinGameRequest(playerColor, gameID, newAuthToken);
    }
}

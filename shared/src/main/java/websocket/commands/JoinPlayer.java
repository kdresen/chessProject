package websocket.commands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    ChessGame.TeamColor teamColor;

    public JoinPlayer(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor color) {
        super(commandType, authToken, gameID);
        this.teamColor = color;
    }

    public ChessGame.TeamColor getTeamColor() {

        return teamColor;
    }
}

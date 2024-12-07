package websocket.commands;

public class LeaveGameCommand extends UserGameCommand {
    int gameID;

    public LeaveGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}

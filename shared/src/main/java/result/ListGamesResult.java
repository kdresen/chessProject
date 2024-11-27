package result;

import model.GameData;

import java.util.ArrayList;
import java.util.List;

public record ListGamesResult(List<GameData> games) {
    public List<String> listGameInfo() {
        List<String> gameInfo = new ArrayList<>();
        games.forEach(game -> {
            gameInfo.add(game.gameName());

            String whiteUsername = game.whiteUsername() != null ? game.whiteUsername() : "empty";
            String blackUsername = game.blackUsername() != null ? game.blackUsername() : "empty";

            gameInfo.add(whiteUsername);
            gameInfo.add(blackUsername);
        });

        return gameInfo;
    }
}


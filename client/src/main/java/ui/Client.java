package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import ui.server.ServerFacade;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Client {

    // for login here is username password, tries to log in, saves auth token, changes state
    // all in try catch block, if throws error, instead return a string with the error

    private String userName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private State inGame = State.SIGNEDOUT;
    private String authToken;
    private List<GameData> fullGameList;
    private List<String> mostRecentGamesList;


    public Client(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = null;
        this.mostRecentGamesList = null;
        this.fullGameList = null;
    }

    public State getState() {
        return state;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.SIGNEDOUT) {
                return switch(cmd) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else if (state == State.SIGNEDIN) {
                return switch(cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames(params);
                    case "join" -> joinGame(params);
                    case "observe" -> observe(params);
                    case "logout" -> logout(params);
                    case "delete" -> admin(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            } else {
                return switch(cmd) {
                    case "quit" -> "quit";
                    default -> help();
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            // call the login function from server facade
            var result = server.loginUser(new UserData(params[0], params[1], null));
            System.out.println(result);
            authToken = result.authToken();
            state = State.SIGNEDIN;
            userName = params[0];
            // get the game list for joining games
            var newResult = server.listGames(authToken);
            fullGameList = newResult.games();
            mostRecentGamesList = newResult.listGameInfo();
            return "Successfully logged in.";
        }
        return "something went wrong"; // TODO handle errors
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            // call the login function from server facade
            var result = server.registerUser(params[0], params[1], params[2]);
            System.out.println(result);
            // save auth
            authToken = result.authToken();
            state = State.SIGNEDIN;
            // get the games list for joining games
            var newResult = server.listGames(authToken);
            fullGameList = newResult.games();
            mostRecentGamesList = newResult.listGameInfo();
            return "Successfully registered.";
        }
        return "To register a new user, enter \"register YOUR USERNAME YOUR PASSWORD YOUR EMAIL\"";
        // TODO handle errors

    }

    public String logout(String... params) throws ResponseException {
        assertSignedIn();
        if (authToken != null) {
            server.logoutUser(authToken);
            authToken = null;
            userName = null;
            inGame = State.CHESSGAME;
            return "Successfully logged out.";
        }


        return "fix this error handling idiot."; // TODO
    }
    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var result = server.createGame(params[0], authToken);
            System.out.println(result);
            var newResult = server.listGames(authToken);
            fullGameList = newResult.games();
            mostRecentGamesList = newResult.listGameInfo();
            return "Successfully created " + params[0];
        }

        return "FIX THIS ERROR HANDLING FOR CREATE GAME"; // TODO
    }
    public String listGames(String... params) throws ResponseException {
        assertSignedIn();
        // get games list
        var result = server.listGames(authToken);

        System.out.println(result);

        var smallerResult = result.listGameInfo();
        fullGameList = result.games();
        mostRecentGamesList = smallerResult;

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < smallerResult.size(); i += 3) {
            String gameName = smallerResult.get(i);
            String whiteUsername = smallerResult.get(i + 1);
            String blackUsername = smallerResult.get(i + 2);

            output.append(i / 3 + 1)
                    .append(".   Game name: ").append(gameName)
                    .append(" White: ").append(whiteUsername)
                    .append(" Black: ").append(blackUsername)
                    .append("\n");
        }
        return output.toString().trim();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        // add check to make sure games list isn't empty
        if (params.length >= 1) {
            ChessGame.TeamColor color;
            int gameNumber = Integer.parseInt(params[0]);
            int gameID = getGameIDFromNumber(gameNumber);
            String gameName = getGameNameFromIndex(gameNumber);

            // get the player color
            String chosenColor = params[1];
            if (Objects.equals(chosenColor, "w") || Objects.equals(chosenColor, "b")) {
                color = Objects.equals(chosenColor, "w") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            } else {
                return "Please choose a color from the provided options";
            }

            // add the player to the game
            server.joinGame(gameID, color, authToken);
            return "Successfully joined " + gameName; // TODO change this to draw the game

        }

        return "there was the wrong amount of expected parameters"; // TODO
    }
    public String observe(String... params) throws ResponseException {
        assertSignedIn();

        int gameID = getGameIDFromNumber(Integer.parseInt(params[0]));

        // draw the game


        return null; // TODO
    }

    private String getGameNameFromIndex(int index) {
        return mostRecentGamesList.get(index);
    }

    private int getGameIDFromNumber(int gameNumber) {
        int gameIndex = (gameNumber - 1) * 3;
        if (gameIndex < 0 || gameIndex >= mostRecentGamesList.size()) {
            return 0;
        }
        // get the gameName
        String gameName = getGameNameFromIndex(gameIndex);
        // get the gameID
        GameData game = fullGameList.stream().filter(g -> Objects.equals(g.gameName(), gameName))
                .findFirst().orElse(null);
        assert game != null;

        return game.gameID();
    }

    public String admin(String... params) throws ResponseException {
        assertSignedIn();
        server.clearDatabases();
        return "deleted stuff";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                        login <USERNAME> <PASSWORD> - to play chess
                        quit - playing chess
                        help - with possible commands
                    """;
        }
        return """
                    create a game: <NAME> "new_game"
                    list all games: "list"
                    join a game: "join" <GAME NUMBER> "1" <COLOR> "w" or "b"
                    observe a game: <GAME NUMBER> "1"
                    logout: "logout"
                    help: "help"
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

}

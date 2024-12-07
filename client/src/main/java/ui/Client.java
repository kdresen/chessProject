package ui;

import chess.ChessGame;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import ui.server.ServerFacade;
import ui.websocket.ServerMessageHandler;
import ui.websocket.WebsocketCommunicator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import static ui.DrawChessBoard.createChessBoard;

public class Client {

    // for login here is username password, tries to log in, saves auth token, changes state
    // all in try catch block, if throws error, instead return a string with the error

    private String userName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private List<GameData> fullGameList;
    private List<String> mostRecentGamesList;
    ChessGame currentGame;
    private ChessGame.TeamColor clientColor;
    public static DrawChessBoard drawChessBoard;
    private final String serverUrl;

    private WebsocketCommunicator ws;
    private final ServerMessageHandler serverMessageHandler;


    public Client(String serverUrl, ServerMessageHandler serverMessageHandler) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = null;
        this.mostRecentGamesList = null;
        this.fullGameList = null;
        this.currentGame = null;
        this.clientColor = null;
        this.serverMessageHandler = serverMessageHandler;
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
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "observe" -> observe(params);
                    case "logout" -> logout();
                    case "delete" -> admin();
                    case "quit" -> "quit";
                    default -> help();
                };
            } else if (state == State.INGAME){
                return switch(cmd) {
                    case "redraw" -> redraw();
                    case "show" -> show(params);
                    case "move" -> makeMove(params);
                    case "leave" -> leaveGame();
                    case "resign" -> resignGame();
                    case "quit" -> "quit";
                    default -> help();
                };
            } else {
                return leaveObserver();
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            // call the login function from server facade
            try {
                var result = server.loginUser(new UserData(params[0], params[1], null));
                userName = result.username();
                authToken = result.authToken();
            } catch (ResponseException ex) {
                return "Incorrect Login or Password";
            }



            state = State.SIGNEDIN;

            // get the game list for joining games
            var newResult = server.listGames(authToken);
            fullGameList = newResult.games();
            mostRecentGamesList = newResult.listGameInfo();
            return "Welcome " + userName + ". Type help to get started";
        }
        return "Incorrect Login Info. example = login james james123! (spaces in usernames and passwords are not allowed)";
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            // call the login function from server facade
            try {
                var result = server.registerUser(params[0], params[1], params[2]);
                // save auth
                authToken = result.authToken();
                userName = result.username();
            } catch (ResponseException ex) {
                return "Username is unavailable. Please try another username.";
            }

            state = State.SIGNEDIN;
            // get the games list for joining games
            var newResult = server.listGames(authToken);
            fullGameList = newResult.games();
            mostRecentGamesList = newResult.listGameInfo();
            return "Welcome " + userName + ". Type help to get started.";
        }
        return """
        To register a new user, enter: register <YOUR USERNAME> <YOUR PASSWORD> <YOUR EMAIL>
        No spaces are allowed in usernames, emails, or passwords.
        """;
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        if (authToken != null) {
            try {
                server.logoutUser(authToken);
            } catch (ResponseException ex) {
                return ex.getMessage();
            }

            authToken = null;
            userName = null;
            state = State.SIGNEDOUT;
            return "Successfully logged out.";
        }


        return "You must be logged in to logout.";
    }
    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            server.createGame(params[0], authToken);
            var newResult = server.listGames(authToken);
            fullGameList = newResult.games();
            mostRecentGamesList = newResult.listGameInfo();
            return "Successfully created " + params[0];
        }

        return """
                To create a game, enter: create <GAME NAME> (example: create new_game)
                No spaces are allowed in game names.
                """;
    }
    public String listGames() throws ResponseException {
        assertSignedIn();
        // get games list
        var result = server.listGames(authToken);

        var smallerResult = result.listGameInfo();
        fullGameList = result.games();
        mostRecentGamesList = smallerResult;
        if (fullGameList.isEmpty()) {
            return "No games are available. Type help to get started.";
        }

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

    public String joinGame(String... params) {
        try {
            assertSignedIn();
        } catch (ResponseException ex) {
            return "Please sign in";
        }

        // add check to make sure games list isn't empty
        if (params.length >= 1) {
            if (params.length == 1) {
                return "Please enter the game number and team color (example: join 1 w)";
            }
            ChessGame.TeamColor color;
            try {
                Integer.parseInt(params[0]);
            } catch (NumberFormatException ex) {
                return "Please use the game number to join";
            }
            int gameNumber = Integer.parseInt(params[0]);
            if (gameNumber == 0 || gameNumber > fullGameList.size()) {
                return "Please choose a game number from the list";
            }
            GameData game = getGameFromNumber(gameNumber);
            assert game != null;
            int gameID = game.gameID();
            String gameName = getGameNameFromIndex(gameNumber);

            // get the player color
            String chosenColor = params[1];
            if (Objects.equals(chosenColor, "w") || Objects.equals(chosenColor, "b")) {
                color = Objects.equals(chosenColor, "w") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            } else {
                return "Please choose a color from the provided options \nThe available options are w or b";
            }

            // add the player to the game
            try {
                server.joinGame(gameID, color, authToken);
            } catch (ResponseException ex) {
                if (ex.getStatusCode() == 403) {
                    return "This color is already taken, please choose another, or create a new game.";
                }
                if (ex.getStatusCode() == 401) {
                    return "Please logout and log in again.";
                }
                if (ex.getStatusCode() == 500) {
                    return "Unable to contact server, please try again later.";
                }
            }

            try {
                ws = new WebsocketCommunicator(serverUrl, serverMessageHandler);
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }


            clientColor = color;
            currentGame = game.game();
            createChessBoard(game.game(), color);
            state = State.INGAME;
            return "Successfully joined " + gameName;

        }

        return "Please enter the game number and team color (example: join 1 w) \n To view the list of games, enter \"list\"";
    }
    public String observe(String... params) throws ResponseException {
        try {
            assertSignedIn();
        } catch (ResponseException ex) {
            int statusCode = ex.getStatusCode();
            if (statusCode == 401) {
                return "Unauthorized";
            }
        }
        if (Integer.parseInt(params[0]) == 0 || Integer.parseInt(params[0]) > fullGameList.size()) {
            return "Please choose a game number from the list";
        }
        GameData game = getGameFromNumber(Integer.parseInt(params[0]));

        // draw the game
        assert game != null;
        clientColor = ChessGame.TeamColor.WHITE;
        currentGame = game.game();
        createChessBoard(game.game(), ChessGame.TeamColor.WHITE);

        return "Observing " + game.gameName();
    }



    // In Game Commands

    public String redraw() throws ResponseException {
        try {
            assertInGame();
        } catch (ResponseException ex) {
            int statusCode = ex.getStatusCode();
            if (statusCode == 401) {
                return "Unauthorized";
            }
        }
        // draw the game board from the client's saved color
        createChessBoard(currentGame, clientColor);

        return "Board updated";
    }

    public String show(String... params) throws ResponseException {
        // prints out the board but with highlighted spaces for legal moves
        assertInGame();

        if (params.length == 1 && params[0].matches("[a-h][1-8]")) {
            ChessPosition position = new ChessPosition(params[0].charAt(0) - '0', params[0].charAt(0) - ('a'-1));
        }

        return null;
    }

    public String makeMove(String... params) throws ResponseException {
        // updates ChessGame in the database with the new position of the piece
        assertInGame();

        return null;
    }

    public String leaveGame(String... params) throws ResponseException {
        assertInGame();

        // leave the game

        return null;
    }

    public String leaveObserver(String... params) throws ResponseException {
        // leave the game as observer

        state = State.SIGNEDIN;

        return "Left Game";
    }

    public String resignGame(String... params) throws ResponseException {
        assertInGame();
        // get confirmation

        // resign the game

        // game is over, but doesn't force user to leave game

        return null;
    }





    private String getGameNameFromIndex(int index) {
        return mostRecentGamesList.get(index);
    }

    public void updateCurrentGame(GameData game) {
        currentGame = game.game();
    }

    private GameData getGameFromNumber(int gameNumber) {
        int gameIndex = (gameNumber - 1) * 3;
        if (gameIndex < 0 || gameIndex >= mostRecentGamesList.size()) {
            return null;
        }
        // get the gameName
        String gameName = getGameNameFromIndex(gameIndex);
        // get the gameID
        GameData game = fullGameList.stream().filter(g -> Objects.equals(g.gameName(), gameName))
                .findFirst().orElse(null);
        assert game != null;

        return game;
    }

    public String admin() throws ResponseException {
        assertSignedIn();
        server.clearDatabases();
        state = State.SIGNEDOUT;
        return "deleted stuff";
    }

    public String help() {
        if (state == State.INGAME) {
            return """
                        redraw chess board:     redraw
                        show legal moves;       show <PIECE POSITION>       (example: e6)
                        make a move:            move <NEW POSITION>         (example: e6)
                        leave the game:         leave
                        resign the game:        resign
                    """;
        }
        else if (state == State.SIGNEDIN) {
            return """
                        create a game:      create <NAME>                               (example: create new_game)
                        list all games:     list
                        join a game:        join <GAME NUMBER> <COLOR> [w|b]            (example: join 1 w)
                        observe a game:     observe <GAME NUMBER>                       (example: observe 1)
                        logout:             logout
                        help:               help
                    """;
        }
        else if (state == State.SIGNEDOUT) {
            return """
                        register a new user:    register <USERNAME> <PASSWORD> <EMAIL>
                        login to your account:  login <USERNAME> <PASSWORD>
                        quit:                   quit
                        help:                   help
                    """;
        }
        else {
            return """
                        leave game:             leave
                    """;
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertInGame() throws ResponseException {
        if (state == State.INGAME) {
            throw new ResponseException(400, "Please Join A Game To Make this Command");
        }
    }

}

package ui;

import exception.ResponseException;
import model.UserData;
import ui.server.ServerFacade;

import java.util.Arrays;

public class Client {

    // for login here is username password, tries to login, saves authtoken, changes state
    // all in try catch block, if throws error, instead return a string with the error

    private String userName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;


    public Client(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
            }
            return switch(cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            // call the login function from server facade
            var result = server.loginUser(new UserData(params[0], params[1], params[2]));
            System.out.println(result);
        }
        return null;
    }

    public String register(String... params) throws ResponseException {
        return null; // TODO
    }
    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        return null; // TODO
    }
    public String listGames(String... params) throws ResponseException {
        assertSignedIn();
        return null; // TODO
    }
    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        return null; // TODO
    }
    public String observe(String... params) throws ResponseException {
        assertSignedIn();
        return null; // TODO
    }
    public String logout(String... params) throws ResponseException {
        assertSignedIn();
        return null; // TODO
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
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

}

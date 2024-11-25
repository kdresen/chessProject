package ui;

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
                    case "quit" -> quit();
                    default -> help();
                };
            }
            return switch(cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> quit();
                default -> help();
            }

        }
    }


}

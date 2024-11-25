package ui;

import ui.server.ServerFacade;

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


}

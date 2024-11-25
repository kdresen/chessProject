package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    // just logic of spits out prompt and takes in input and tells client to evaluate the stuff, client does stuff and returns result
    // all logic handled in the client
    // repl just prints out strings
    // client returns all strings
    // run in client starts repl and waits for the state to be quit
    // if state is quit, ends repl
    // repl takes in input from user and evaluates it
    // switch statement determines how to evaluate it

    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to the Repl Client!");
        //System.out.println(client.help());
    }

}

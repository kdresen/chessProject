package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    // just logic of spits out prompt and takes in input and tells client to
    // evaluate the stuff, client does stuff and returns result
    // all logic handled in the client
    // repl just prints out strings
    // client returns all strings
    // run in client starts repl and waits for the state to be quit
    // if state is quit, ends Repl
    // repl takes in input from user and evaluates it
    // switch statement determines how to evaluate it

    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {

        System.out.println("♕ Welcome to 240 chess! Type help to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + client.getState()
                + "] " +  ">>> " + SET_TEXT_COLOR_GREEN);
    }

}

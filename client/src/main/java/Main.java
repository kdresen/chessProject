import ui.Repl;


public class Main {

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}
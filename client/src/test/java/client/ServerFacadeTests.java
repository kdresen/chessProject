package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import result.CreateGamesResult;
import result.RegisterResult;
import server.Server;
import ui.server.ServerFacade;

import static chess.ChessGame.TeamColor.WHITE;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);

    }

    @BeforeEach
    public void clearDatabase() {
        facade.clearDatabases();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void registerPositive() throws Exception {
        var authData = facade.registerUser("player1", "password", "email");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerNegative() throws Exception {
        facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.registerUser("player2", "password", "email"), "Should not allow duplicate usernames");
    }

    @Test
    public void loginPositive() throws Exception {
        facade.registerUser("player2", "password", "email");
        var authData = facade.loginUser(new UserData("player2", "password", null));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginNegative() throws Exception {
        facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.loginUser(new UserData("player2", "badPassword", null)));
    }

    @Test
    public void logoutPositive() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        AuthData validAuth = new AuthData(authData.authToken(), authData.username());
        assertDoesNotThrow(() -> facade.logoutUser(validAuth.authToken()));
    }

    @Test
    public void logoutNegative() throws Exception {
        facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.logoutUser("badAuthToken"));
    }

    @Test
    public void createGamePositive() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        assertDoesNotThrow(() -> {
            CreateGamesResult result = facade.createGame("testGame", authData.authToken());
            assertNotNull(result);
        });

    }

    @Test
    public void createGameNegative() throws Exception {
        facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.createGame("testGame", "badAuthToken"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        assertDoesNotThrow(() -> facade.listGames(authData.authToken()));
    }

    @Test
    public void listGamesNegative() throws Exception {
        facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.listGames("badAuthToken"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        RegisterResult authDataTwo = facade.registerUser("player3", "password", "email");
        CreateGamesResult createGamesResult = facade.createGame("testGame", authData.authToken());
        facade.joinGame(createGamesResult.gameID(), WHITE , authData.authToken());
        assertThrows(ResponseException.class, () -> facade.joinGame(createGamesResult.gameID(), WHITE , authDataTwo.authToken()));
    }

    @Test
    public void joinGameNegative() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        CreateGamesResult createGamesResult = facade.createGame("testGame", authData.authToken());
        assertThrows(ResponseException.class, () -> facade.joinGame(createGamesResult.gameID(), WHITE, "badAuthToken"));
    }

}

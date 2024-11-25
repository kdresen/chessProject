package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import result.RegisterResult;
import server.Server;
import ui.server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static Gson gson;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        gson = new Gson();
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
        var userData = facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.registerUser("player2", "password", "email"), "Should not allow duplicate usernames");
    }

    @Test
    public void loginPositive() throws Exception {
        var userData = facade.registerUser("player2", "password", "email");
        var authData = facade.loginUser(new UserData("player2", "password", null));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void loginNegative() throws Exception {
        var userData = facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.loginUser(new UserData("player2", "badPassword", null)));
    }

    @Test
    public void logoutPositive() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        AuthData validAuth = new AuthData(authData.authToken(), authData.username());
        assertDoesNotThrow(() -> {
            facade.logoutUser(validAuth.authToken());
        });
    }

    @Test
    public void logoutNegative() throws Exception {
        RegisterResult authData = facade.registerUser("player2", "password", "email");
        assertThrows(ResponseException.class, () -> facade.logoutUser("badAuthToken"));
    }

    @Test



}

package service;

import dataaccess.DataAccessException;
import dataaccess.daointerfaces.*;
import dataaccess.memorydao.MemoryAuthDAO;
import dataaccess.memorydao.MemoryGameDAO;
import dataaccess.memorydao.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

import java.util.UUID;

import static chess.ChessGame.TeamColor.WHITE;
import static org.junit.jupiter.api.Assertions.*;



public class ServiceTests {
    private UserDAO testUserDAO;
    private AuthDAO testAuthDAO;
    private GameDAO testGameDAO;
    private AdminService adminService;
    private GameService gameService;
    private UserService userService;
    private AuthData testAuth;
    private GameData testGame;
    private UserData testUser;


    private RegisterRequest testRegisterRequest;
    private CreateGamesRequest testCreateGamesRequest;
    private JoinGameRequest testJoinGameRequest;
    private ListGamesRequest testListGamesRequest;
    private LoginRequest testLoginRequest;
    private LoginRequest testBadLoginRequest;

    private CreateGamesResult testCreateGamesResult;
    private JoinGameResult testJoinGameResult;
    private LoginResult testLoginResult;
    private ListGamesResult testListGamesResult;
    private RegisterResult testRegisterResult;

    private int gameID;

    @BeforeEach
    void setUp() {
        testUserDAO = new MemoryUserDAO();
        testAuthDAO = new MemoryAuthDAO();
        testGameDAO = new MemoryGameDAO();
        adminService = new AdminService(testUserDAO, testAuthDAO, testGameDAO);
        gameService = new GameService(testGameDAO, testAuthDAO);
        userService = new UserService(testUserDAO, testAuthDAO);


        try {
            testAuth = testAuthDAO.createAuthData(new AuthData(UUID.randomUUID().toString() ,"username"));
            testGame = testGameDAO.getGameByID(testGameDAO.createGame("gameName"));
            testUser = testUserDAO.getUserByUsername(testUserDAO.createUser(new UserData("username", "password", "email")));
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }

        testCreateGamesRequest = new CreateGamesRequest(testGame.gameName());
        testJoinGameRequest = new JoinGameRequest(WHITE, testGame.gameID());
        testRegisterRequest = new RegisterRequest(testUser.username(), testUser.password(), testUser.email());
        testLoginRequest = new LoginRequest(testUser.username(), testUser.password());
        testBadLoginRequest = new LoginRequest(testUser.username(), "bad password");
        testListGamesRequest = new ListGamesRequest(testAuth.authToken());
    }

    @Test
    void testClearApplicationPositive() {

        assertDoesNotThrow(() -> adminService.clearApplication());
        assertThrows(DataAccessException.class, () -> userService.login(testLoginRequest));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(testJoinGameRequest, testAuth.authToken()));
    }

    @Test
    void testLoginPositive() {

        assertDoesNotThrow(() -> userService.login(testLoginRequest));

    }

    @Test
    void testLoginNegative() {
        assertThrows(DataAccessException.class, () -> userService.login(testBadLoginRequest));
    }

    @Test
    void testRegisterPositive() {
        try {
            testUserDAO.deleteAllUsers();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        assertDoesNotThrow(() -> userService.register(testRegisterRequest));
    }

    @Test
    void testRegisterNegative() {
        assertThrows(DataAccessException.class, () -> userService.register(testRegisterRequest));
    }

    @Test
    void testLogoutPositive() {
        assertDoesNotThrow(() -> userService.logout(testAuth.authToken()));
    }

    @Test
    void testLogoutNegative() {
        try {
            testAuthDAO.deleteAllAuthData();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        assertThrows(DataAccessException.class, () -> userService.logout(testAuth.authToken()));
    }

    @Test
    void testCreateGamePositive() {
        assertDoesNotThrow(() -> gameService.createGame(testCreateGamesRequest, testAuth.authToken()));
    }

    @Test
    void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () -> gameService.createGame(testCreateGamesRequest, "bad authToken"));
    }

    @Test
    void testJoinGamePositive() {
        assertDoesNotThrow(() -> gameService.joinGame(testJoinGameRequest, testAuth.authToken()));
    }

    @Test
    void testJoinGameNegative() {
        assertDoesNotThrow(() -> gameService.joinGame(testJoinGameRequest, testAuth.authToken()));
        assertThrows(DataAccessException.class, () -> gameService.joinGame(testJoinGameRequest, testAuth.authToken()));
    }

    @Test
    void testListGamesPositive() {
        assertDoesNotThrow(() -> gameService.listGames(testListGamesRequest));
    }

    @Test
    void testListGamesNegative() {
        assertThrows(DataAccessException.class, () -> gameService.listGames(new ListGamesRequest("bad authToken")));
    }
}

package server;

import com.google.gson.Gson;

import dataaccess.DataAccessException;

import model.GameData;
import model.UserData;
import request.*;
import result.*;
import server.websocket.WebSocketHandler;
import org.eclipse.jetty.websocket.api.Session;
import service.AdminService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static UserService userService;
    public static GameService gameService;
    public static AdminService adminService;
    private final Gson gson = new Gson();
    static final boolean SQL = true;

    public static ConcurrentHashMap<Session, Integer> gameSessions = new ConcurrentHashMap<>();

    static {
        if (SQL) {
            userService = new UserService(true);
            gameService = new GameService(true);
            adminService = new AdminService(true);
        } else {
            userService = new UserService(false);
            gameService = new GameService(false);
            adminService = new AdminService(false);
        }
    }

    public Server() {}


    public int run(int desiredPort) {


        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/connect", WebSocketHandler.class);

        Spark.notFound("<html><body>My custom 404 page</body></html>");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::deleteAll);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object exceptionHandler(DataAccessException ex, Request req, Response res) {
        switch (ex.getMessage()) {
            case "Error: bad request":
                res.status(400);
                break;
            case "Error: unauthorized":
                res.status(401);
                break;
            case "Error: already taken":
                res.status(403);
                break;
            default:
                res.status(500);

        }
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
        return res.body();
    }

    private Object deleteAll(Request req, Response res) {
        try {
            adminService.clearApplication();
            return gson.toJson(new HashMap<>());
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }
    private Object registerUser(Request req, Response res) {
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            RegisterResult result = userService.register(new RegisterRequest(userData.username(), userData.password(), userData.email()));
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }

    }
    private Object loginUser(Request req, Response res) {
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            LoginResult result = userService.login(new LoginRequest(userData.username(), userData.password()));
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }
    private Object logoutUser(Request req, Response res) {
        try {
            userService.logout(req.headers("authorization"));
            return gson.toJson(new HashMap<>());
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }
    private Object listGames(Request req, Response res) {
        try {
            ListGamesRequest request = new ListGamesRequest(req.headers("authorization"));
            ListGamesResult result = gameService.listGames(request);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }
    private Object createGame(Request req, Response res) {
        try {
            GameData newGame = new Gson().fromJson(req.body(), GameData.class);
            CreateGamesRequest request = new CreateGamesRequest(newGame.gameName());
            CreateGamesResult result = gameService.createGame(request, req.headers("authorization"));
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }
    private Object joinGame(Request req, Response res) {
        try {
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
            JoinGameResult result = gameService.joinGame(request, req.headers("authorization"));
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }


}

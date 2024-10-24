package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryGameDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.AdminService;
import service.GameService;
import service.UserService;
import spark.*;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final AdminService adminService;
    private final Gson gson = new Gson();


    public Server() {
        this.userService = new UserService();
        this.gameService = new GameService();
        this.adminService = new AdminService();
        UserDAO userDAO = MemoryUserDAO.getInstance();
        AuthDAO authDAO = MemoryAuthDAO.getInstance();
        GameDAO gameDAO = MemoryGameDAO.getInstance();
    }


    public int run(int desiredPort) {


        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.notFound("<html><body>My custom 404 page</body></html>");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::deleteAll);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
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
            AuthData authData = new Gson().fromJson(req.headers("authorization"), AuthData.class);
            userService.logout(new LogoutRequest(authData.authToken()));
            return null;
        } catch (DataAccessException ex) {
            return exceptionHandler(ex, req, res);
        }
    }


}

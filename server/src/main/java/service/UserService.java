package service;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.memorydao.MemoryAuthDAO;
import dataaccess.memorydao.MemoryUserDAO;
import dataaccess.sqldao.MySQLAuthDAO;
import dataaccess.sqldao.MySQLUserDAO;
import dataaccess.daointerfaces.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        if (Objects.equals(registerRequest, null)) {
            throw new DataAccessException("Error: bad request");
        }
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData user = userDAO.getUserByUsername(registerRequest.username());
        if (user != null) {
            throw new DataAccessException("Error: already taken");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        String username = userDAO.createUser(newUser);
        AuthData authData = new AuthData(UUID.randomUUID().toString() ,registerRequest.username());
        AuthData authResult = authDAO.createAuthData(authData);

        return new RegisterResult(username, authResult.authToken());

    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        //UserData user = userDAO.getUserByUsername(loginRequest.username());
        if (loginRequest.password() == null || loginRequest.username() == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (userDAO.verifyUser(loginRequest.username(), loginRequest.password())) {
            AuthData authResult = authDAO.createAuthData(new AuthData(UUID.randomUUID().toString(), loginRequest.username()));
            return new LoginResult(authResult.username(), authResult.authToken());
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
    public void logout(String authToken) throws DataAccessException {

        AuthData auth = authDAO.getAuthData(authToken);
        if (auth != null) {
            authDAO.deleteAuthData(auth.authToken());
        } else {
            throw new DataAccessException("Error: unauthorized");
        }

    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuthData(authToken);
    }


}

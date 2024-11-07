package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import dataaccess.SQLDAO.MySQLAuthDAO;
import dataaccess.SQLDAO.MySQLUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import javax.xml.crypto.Data;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(boolean isSQL) {
        if (isSQL) {
            this.userDAO = new MySQLUserDAO();
            this.authDAO = new MySQLAuthDAO();
        } else {
            this.userDAO = MemoryUserDAO.getInstance();
            this.authDAO = MemoryAuthDAO.getInstance();
        }

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
        UserData user = userDAO.getUserByUsername(loginRequest.username());
        if (userDAO.verifyUser(loginRequest.username(), loginRequest.password())) {
            AuthData authResult = authDAO.createAuthData(new AuthData(UUID.randomUUID().toString(), user.username()));
            return new LoginResult(authResult.username(), authResult.authToken());
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException {

        AuthData auth = authDAO.getAuthData(logoutRequest.authToken());
        if (auth != null) {
            authDAO.deleteAuthData(auth.authToken());
        } else {
            throw new DataAccessException("Error: unauthorized");
        }

    }


}

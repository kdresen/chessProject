package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        this.userDAO = MemoryUserDAO.getInstance();
        this.authDAO = MemoryAuthDAO.getInstance();
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
        UserData newUser = new UserData(registerRequest.username(), registerRequest.email(), registerRequest.password());
        String username = userDAO.createUser(newUser);
        AuthData authData = new AuthData(UUID.randomUUID().toString() ,registerRequest.username());
        AuthData authResult = authDAO.createAuthData(authData);

        return new RegisterResult(username, authResult.authToken());

    }
    public LoginResult login(LoginRequest loginRequest) {
        try {
            UserData user = userDAO.getUserByUsername(loginRequest.username());
            if (user != null && Objects.equals(user.password(), loginRequest.password())) {
                AuthData authResult = authDAO.createAuthData(new AuthData(loginRequest.username(), loginRequest.authToken()));
                return new LoginResult(user.username(), authResult.authToken());
            } else {
                // TODO fix this
                throw new DataAccessException("Error: unauthorized");
            }

        } catch (DataAccessException e) {
            // TODO fix this
            throw new RuntimeException(e);
        }
    }
    public void logout(LogoutRequest logoutRequest) {
        try {
            AuthData auth = authDAO.getAuthData(logoutRequest.authToken());
            if (auth != null) {
                authDAO.deleteAuthData(auth.authToken());
            } else {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


}

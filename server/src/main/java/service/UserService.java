package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        try {
            UserData newUser = new UserData(registerRequest.username(), registerRequest.email(), registerRequest.password());
            String username = userDAO.createUser(newUser);
            AuthData authData = new AuthData(UUID.randomUUID().toString() ,registerRequest.username());
            AuthData authResult = authDAO.createAuthData(authData);

            return new RegisterResult(username, authResult.authToken());
        } catch (DataAccessException e) {
            // TODO fix this
            throw new RuntimeException();
        }
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

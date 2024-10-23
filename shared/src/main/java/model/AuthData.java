package model;

import com.google.gson.Gson;

public record AuthData(String authToken, String username) {
    AuthData rename(String newUsername) {
        return new AuthData(authToken, newUsername);
    }
    AuthData replaceUsername(String newUsername) {
        return new AuthData(authToken, newUsername);
    }
    public AuthData replaceAuthToken(String newAuthToken) {
        return new AuthData(newAuthToken, username);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}

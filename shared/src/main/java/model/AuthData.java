package model;

public record AuthData(String authToken, String username) {
    AuthData rename(String newUsername) {
        return new AuthData(authToken, newUsername);
    }
    AuthData replaceUsername(String newUsername) {
        return new AuthData(authToken, newUsername);
    }
}

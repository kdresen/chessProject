package model;

public record UserData(String username, String password, String email) {
    UserData replaceUsername(String newUsername) {
        return new UserData(newUsername, password, email);
    }
    UserData replacePassword(String newPassword) {
        return new UserData(username, newPassword, email);
    }
    UserData replaceEmail(String newEmail) {
        return new UserData(username, password, newEmail);
    }
}

package dataaccess;

import model.UserData;

import java.util.List;

public interface UserDAO {
    // add a new user to the database
    void createUser(UserData user) throws DataAccessException;

    // retrieve a user by their username
    UserData getUserByUsername(String username) throws DataAccessException;

    // retrieve a list of all users
    List<UserData> getAllUsers() throws DataAccessException;

    // delete all users
    void deleteAllUsers() throws DataAccessException;
}

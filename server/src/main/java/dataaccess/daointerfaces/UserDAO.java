package dataaccess.daointerfaces;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {

    // add a new user to the database
    String createUser(UserData user) throws DataAccessException;

    // retrieve a user by their username
    UserData getUserByUsername(String username) throws DataAccessException;

    boolean verifyUser(String username, String password) throws DataAccessException;

    // delete all users
    void deleteAllUsers() throws DataAccessException;
}

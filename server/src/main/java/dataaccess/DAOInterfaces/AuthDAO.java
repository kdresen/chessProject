package dataaccess.DAOInterfaces;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    // create or insert authentication data for a user
    // returns a string authToken
    AuthData createAuthData(AuthData authData) throws DataAccessException;

    // retrieve authentication data by username
    AuthData getAuthData(String authToken) throws DataAccessException;

    // delete AuthData from database
    void deleteAuthData(String authToken) throws DataAccessException;

    // delete all AuthData
    void deleteAllAuthData() throws DataAccessException;
}

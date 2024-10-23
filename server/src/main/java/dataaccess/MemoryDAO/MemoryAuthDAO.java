package dataaccess.MemoryDAO;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public String createAuthData(String username) throws DataAccessException {
        String authToken = generateToken();

        return "";
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {

    }

    @Override
    public void deleteAllAuthData() throws DataAccessException {

    }

    // create new authToken
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

package dataaccess.MemoryDAO;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public String createAuthData(String username) throws DataAccessException {
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
}

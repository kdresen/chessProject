package dataaccess.SQLDAO;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuthData(AuthData authData) throws DataAccessException {
        return null;
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

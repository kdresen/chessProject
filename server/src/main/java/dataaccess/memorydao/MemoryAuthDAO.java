package dataaccess.memorydao;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {
    private List<AuthData> auths;

    public MemoryAuthDAO() {
        this.auths = new ArrayList<AuthData>();
    }


    @Override
    public AuthData createAuthData(AuthData authData) throws DataAccessException {
        auths.add(authData);
        return authData;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        // cool optional class I learned about
        Optional<AuthData> auth = auths.stream().filter(a -> Objects.equals(a.authToken(), authToken)).findFirst();
        // returns null if the authToken is not found
        return auth.orElse(null);
    }

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {
        auths.removeIf(a -> Objects.equals(a.authToken(), authToken));
    }

    @Override
    public void deleteAllAuthData() throws DataAccessException {
        auths.clear();
    }
}

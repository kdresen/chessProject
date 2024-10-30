package dataaccess.SQLDAO;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.List;

public class MySQLUserDAO implements UserDAO {
    @Override
    public String createUser(UserData user) throws DataAccessException {
        return "";
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        return null;
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        return List.of();
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {

    }
}

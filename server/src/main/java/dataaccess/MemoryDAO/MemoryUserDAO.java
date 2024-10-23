package dataaccess.MemoryDAO;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.List;

public class MemoryUserDAO implements UserDAO {
    @Override
    public void createUser(UserData user) throws DataAccessException {

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

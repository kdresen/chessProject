package dataaccess.memorydao;

import dataaccess.DataAccessException;
import dataaccess.daointerfaces.UserDAO;
import model.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MemoryUserDAO implements UserDAO {
    private static MemoryUserDAO instance;
    private final List<UserData> users;



    private MemoryUserDAO() {
        this.users = new ArrayList<UserData>();
    }

    public static MemoryUserDAO getInstance() {
        if (instance == null) {
            instance = new MemoryUserDAO();
        }
        return instance;
    }

    @Override
    public String createUser(UserData user) throws DataAccessException {
        users.add(user);
        return user.username();
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        // cool optional object I learned about
        Optional<UserData> user = users.stream().filter(u -> Objects.equals(u.username(), username)).findFirst();

        // returns null if the UserData is not found
        return user.orElse(null);
    }

    private String getPasswordByUsername(String username) throws DataAccessException {
        Optional<UserData> user = users.stream().filter(u -> Objects.equals(u.username(), username)).findFirst();

        return user.map(UserData::password).orElse(null);
    }

    @Override
    public boolean verifyUser(String username, String password) throws DataAccessException {
        String storedPassword = getPasswordByUsername(username);
        if (storedPassword == null) {
            return false;
        }
        return storedPassword.equals(password);
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        users.clear();
    }
}

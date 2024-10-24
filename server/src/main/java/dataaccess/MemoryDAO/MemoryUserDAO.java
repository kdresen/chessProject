package dataaccess.MemoryDAO;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
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

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        // returns a copy of the list of users
        return new ArrayList<>(users);
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        users.clear();
    }
}

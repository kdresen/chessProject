package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.daointerfaces.UserDAO;
import dataaccess.DatabaseManager;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static dataaccess.sqldao.DatabaseSetup.configureDatabase;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() {
        configureDatabase();
    }

    @Override
    public String createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password, email) VALUES (?,?,?)";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.email());

            ps.executeUpdate();

            return user.username();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }

            return null;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        var hashedPassword = readHashedPasswordFromDatabase(username);
        if (hashedPassword == null) {
            return false;
        }

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        String sql = "TRUNCATE TABLE users";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

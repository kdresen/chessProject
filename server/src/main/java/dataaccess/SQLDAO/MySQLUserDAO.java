package dataaccess.SQLDAO;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.DatabaseManager;
import model.UserData;
import java.sql.*;

import java.util.List;

public class MySQLUserDAO implements UserDAO {

    @Override
    public String createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?,?,?)";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.email());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getString(1);
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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
        String sql = "TRUNCATE users";
        executeUpdate(sql);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else if (param == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    throw new DataAccessException("Unsupported parameter type: " + param.getClass().getName());
                }
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(50) NOT NULL PRIMARY KEY,
            `password` varchar(255) NOT NULL,
            `email` varchar(100) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET= utf8mbb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

package dataaccess.sqldao;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.sqldao.DatabaseSetup.configureDatabase;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() {
        configureDatabase();
    }
    @Override
    public AuthData createAuthData(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authData.authToken());
            ps.setString(2, authData.username());

            ps.executeUpdate();

            return authData;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auths WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auths WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, authToken);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAllAuthData() throws DataAccessException {
        String sql = "TRUNCATE TABLE auths";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }
}

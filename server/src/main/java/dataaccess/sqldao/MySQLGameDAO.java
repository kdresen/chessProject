package dataaccess.sqldao;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.daointerfaces.GameDAO;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static dataaccess.sqldao.DatabaseSetup.configureDatabase;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?)";
        ChessGame game = new ChessGame();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, gameName);
            var json = new Gson().toJson(game);
            ps.setString(4, json);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return 0;
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");

                    var json = rs.getString("game");
                    var game = new Gson().fromJson(json, ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var gameID = rs.getInt("gameID");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");

                    var json = rs.getString("game");
                    var game = new Gson().fromJson(json, ChessGame.class);
                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return games;
    }

    @Override
    public void insertUser(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException {
        String column = (playerColor == ChessGame.TeamColor.BLACK) ? "blackUsername" : "whiteUsername";
        String sql = "UPDATE games SET " + column + " = ? WHERE gameID = ? AND " + column + " IS NULL";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, gameID);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: already taken");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateChessGame(ChessGame game, Integer gameID) throws DataAccessException {
        String sql = "UPDATE games SET gameName = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String gameJson = new Gson().toJson(game);
            ps.setString(1, gameJson);
            ps.setInt(2, gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        String sql = "DELETE FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameID);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        String sql = "TRUNCATE TABLE games";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

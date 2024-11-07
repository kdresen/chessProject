package dataaccess.SQLDAO;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    public MySQLGameDAO() {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?,?,?)";
        String whiteUsername = null;
        String blackUsername = null;
        ChessGame game = new ChessGame();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, whiteUsername);
            ps.setString(2, blackUsername);
            ps.setString(3, gameName);
            ps.setString(4, gson.toJson(game));

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
        return null;
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void insertUser(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException {

    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {

    }

    @Override
    public void deleteAllGames() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `gameID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
            `whiteUsername` varchar(255),
            `blackUsername` varchar(255),
            `gameName` varchar(255) NOT NULL,
            `game` JSON NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET= utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() {

        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var ps = conn.prepareStatement(statement)) {
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.out.printf(e.getMessage());
        }

    }
}

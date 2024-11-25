package dataaccess;
import chess.ChessGame;
import dataaccess.sqldao.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTests {
    private static MySQLGameDAO gameDAO;
    private static GameData testGameData;
    private static int testGameID;

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        gameDAO.deleteAllGames();

        testGameID = gameDAO.createGame("Test Game");
        testGameData = gameDAO.getGameByID(testGameID);
    }

    @Test
    void testCreateGamePositive() {
        assertNotEquals(0, testGameID, "Game ID should not be zero after creation");
        assertNotNull(testGameData, "Game data should not be null");
        assertEquals("Test Game", testGameData.gameName());
    }

    @Test
    void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null), "Should not be able to create a game with no name");
    }

    @Test
    void testGetGameByIDPositive() throws DataAccessException {
        GameData retrievedGame = gameDAO.getGameByID(testGameID);
        assertNotNull(retrievedGame, "Game data should not be null");
        assertEquals(testGameID, retrievedGame.gameID(), "Game ID should be the same");
        assertEquals("Test Game", retrievedGame.gameName(), "Game name should be the same");
    }

    @Test
    void testGetGameByIDNegative() throws DataAccessException {
        GameData retrievedGame = gameDAO.getGameByID(9999);
        assertNull(retrievedGame, "Game data should be null");
    }

    @Test
    void testGetAllGamesPositive() throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        assertFalse(games.isEmpty(), "All games should not be empty");
        assertEquals("Test Game", games.getFirst().gameName(), "Game name should be the same");
    }

    @Test
    void testInsertUserPositive() throws DataAccessException {
        gameDAO.insertUser(testGameID, "player1", ChessGame.TeamColor.WHITE);
        GameData updatedGame = gameDAO.getGameByID(testGameID);
        assertEquals("player1", updatedGame.whiteUsername(), "player username did not match");
    }

    @Test
    void testInsertUserNegative() throws DataAccessException {
        gameDAO.insertUser(testGameID, "player1", ChessGame.TeamColor.WHITE);

        assertThrows(DataAccessException.class, () -> gameDAO.insertUser(testGameID, "player2", ChessGame.TeamColor.WHITE),
                "Should not be able to insert as new player in a taken spot");
    }

    @Test
    void testDeleteGamePositive() throws DataAccessException {
        gameDAO.deleteGame(testGameID);
        GameData retrievedGame = gameDAO.getGameByID(testGameID);
        assertNull(retrievedGame, "Game data should be null");
    }

    @Test
    void testDeleteAllGamesPositive() throws DataAccessException {
        gameDAO.deleteAllGames();
        List<GameData> games = gameDAO.getAllGames();
        assertTrue(games.isEmpty(), "all games should be deleted");
    }


}

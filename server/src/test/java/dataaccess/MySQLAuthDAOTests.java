package dataaccess;
import dataaccess.DataAccessException;
import dataaccess.SQLDAO.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTests {
    private static MySQLAuthDAO authDAO;
    private static AuthData testAuthData;

    @BeforeEach
    void setup() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        testAuthData = new AuthData("testToken", "testUser");
        authDAO.deleteAllAuthData();
    }

    @Test
    void testCreateAuthData_Positive() throws DataAccessException {
        AuthData createdAuthData = authDAO.createAuthData(testAuthData);
        assertEquals(testAuthData.authToken(), createdAuthData.authToken(), "AuthToken did not match");
        assertEquals(testAuthData.username(), createdAuthData.username(), "Username did not match");

        AuthData retrievedAuthData = authDAO.getAuthData(testAuthData.authToken());
        assertNotNull(retrievedAuthData, "AuthData was not stored correctly in database");
    }

    @Test
    void testCreateAuthData_Negative() throws DataAccessException {
        authDAO.createAuthData(testAuthData);
        assertThrows(DataAccessException.class, () -> authDAO.createAuthData(testAuthData), "Should not allow duplicate authTokens");
    }

    @Test
    void testGetAuthData_Positive() throws DataAccessException {
        authDAO.createAuthData(testAuthData);

        AuthData retrievedAuthData = authDAO.getAuthData(testAuthData.authToken());
        assertNotNull(retrievedAuthData, "AuthData was not stored correctly in database");
        assertEquals(testAuthData.authToken(), retrievedAuthData.authToken(), "AuthToken did not match");
        assertEquals(testAuthData.username(), retrievedAuthData.username(), "Username did not match");
    }

    @Test
    void testGetAuthData_Negative() throws DataAccessException {

        AuthData retrievedAuthData = authDAO.getAuthData("fake authToken");
        assertNull(retrievedAuthData, "getAuthData incorrectly returned an AuthData object");
    }

    @Test
    void testDeleteAuthData_Positive() throws DataAccessException {
        authDAO.createAuthData(testAuthData);

        authDAO.deleteAuthData(testAuthData.authToken());
        AuthData retrievedAuthData = authDAO.getAuthData(testAuthData.authToken());
        assertNull(retrievedAuthData, "AuthData was not deleted correctly in database");
    }

    @Test
    void testDeleteAllAuthData_Positive() throws DataAccessException {
        authDAO.createAuthData(testAuthData);

        authDAO.deleteAllAuthData();
        AuthData retrievedAuthData = authDAO.getAuthData(testAuthData.authToken());
        assertNull(retrievedAuthData, "AuthData was not deleted correctly in database");
    }
}

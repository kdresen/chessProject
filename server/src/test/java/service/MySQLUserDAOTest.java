package service;
import dataaccess.DataAccessException;
import dataaccess.SQLDAO.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {
    private static MySQLUserDAO userDAO;
    private static UserData testUser;

    @BeforeAll
    static void setup() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        testUser = new UserData("testUser", "testPassword", "testUser@gmail.com");
        userDAO.deleteAllUsers();
    }


    @Test
    void testCreateUser_Positive() throws DataAccessException {
        String createdUsername = userDAO.createUser(testUser);
        assertEquals(testUser.username(), createdUsername, "Created username did not match");

        UserData retrievedUser = userDAO.getUserByUsername(testUser.username());
        assertNotNull(retrievedUser, "User was not found in the database");
    }

    @Test
    void testCreateUser_Negative() {
        assertThrows(DataAccessException.class, () -> userDAO.createUser(testUser), "Should not create duplicate user");

    }

    @Test
    void testGetUserByUsername_Positive() throws DataAccessException {
        userDAO.createUser(testUser);

        UserData retrievedUser = userDAO.getUserByUsername(testUser.username());
        assertNotNull(retrievedUser, "User was not found in the database");
        assertEquals(testUser.username(), retrievedUser.username(), "Username did not match");
        assertEquals(testUser.email(), retrievedUser.email(), "Email did not match");
        assertTrue(userDAO.verifyUser(testUser.username(), testUser.password()));
    }
}

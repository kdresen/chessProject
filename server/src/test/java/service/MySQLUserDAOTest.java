package service;
import dataaccess.DataAccessException;
import dataaccess.SQLDAO.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTest {
    private static MySQLUserDAO userDAO;
    private static UserData testUser;

    @BeforeEach
    void setup() throws DataAccessException {
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
    void testCreateUser_Negative() throws DataAccessException {
        userDAO.createUser(testUser);

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

    @Test
    void testGetUserByUsername_Negative() throws DataAccessException {
        userDAO.createUser(testUser);

        UserData retrievedUser = userDAO.getUserByUsername("newUsername");
        assertNull(retrievedUser, "User incorrectly returned a user");
    }

    @Test
    void testVerifyUser_Positive() throws DataAccessException {

        userDAO.createUser(testUser);

        boolean isVerified = userDAO.verifyUser(testUser.username(), testUser.password());
        assertTrue(isVerified, "User with correct password was not verified");
    }

    @Test
    void testVerifyUser_Negative() throws DataAccessException {
        userDAO.createUser(testUser);

        boolean isVerified = userDAO.verifyUser(testUser.username(), "invalidPassword");
        assertFalse(isVerified, "User with incorrect password was verified");

        isVerified = userDAO.verifyUser("newUser", testUser.password());
        assertFalse(isVerified, "Unregistered user was verified");
    }

    @Test
    void testDeleteAllUsers_Positive() throws DataAccessException {
        userDAO.createUser(testUser);

        userDAO.deleteAllUsers();

        UserData retrievedUser = userDAO.getUserByUsername(testUser.username());
        assertNull(retrievedUser, "User was found in the empty database");
    }
}

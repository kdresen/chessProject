package service;

import dataaccess.DAOInterfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DAOInterfaces.GameDAO;
import dataaccess.MemoryDAO.MemoryAuthDAO;
import dataaccess.MemoryDAO.MemoryGameDAO;
import dataaccess.MemoryDAO.MemoryUserDAO;
import dataaccess.SQLDAO.MySQLAuthDAO;
import dataaccess.SQLDAO.MySQLGameDAO;
import dataaccess.SQLDAO.MySQLUserDAO;
import dataaccess.DAOInterfaces.UserDAO;

public class AdminService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public AdminService(boolean isSQL) {

        if (isSQL) {
            this.userDAO = new MySQLUserDAO();
            this.authDAO = new MySQLAuthDAO();
            this.gameDAO = new MySQLGameDAO();
        } else {
            this.userDAO = MemoryUserDAO.getInstance();
            this.authDAO = MemoryAuthDAO.getInstance();
            this.gameDAO = MemoryGameDAO.getInstance();
        }

    }

    public void clearApplication() throws DataAccessException {
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuthData();
        gameDAO.deleteAllGames();
    }
}

package service;

import dataaccess.daointerfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.daointerfaces.GameDAO;
import dataaccess.memorydao.MemoryAuthDAO;
import dataaccess.memorydao.MemoryGameDAO;
import dataaccess.memorydao.MemoryUserDAO;
import dataaccess.sqldao.MySQLAuthDAO;
import dataaccess.sqldao.MySQLGameDAO;
import dataaccess.sqldao.MySQLUserDAO;
import dataaccess.daointerfaces.UserDAO;

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

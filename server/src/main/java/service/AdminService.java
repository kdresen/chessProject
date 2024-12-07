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


    public AdminService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {


        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;


    }

    public void clearApplication() throws DataAccessException {
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuthData();
        gameDAO.deleteAllGames();
    }
}

package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.SQLException;

public class DatabaseSetup {

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(50) NOT NULL PRIMARY KEY,
            `password` varchar(255) NOT NULL,
            `email` varchar(100) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET= utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            `whiteUsername` varchar(255),
            `blackUsername` varchar(255),
            `gameName` varchar(255) NOT NULL,
            `game` longtext NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET= utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
            `authToken` varchar(255) NOT NULL PRIMARY KEY,
            `username` varchar(50) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET= utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public static void configureDatabase() {

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

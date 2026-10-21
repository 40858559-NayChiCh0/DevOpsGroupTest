package com.napier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final int MAX_RETRIES = 10;
    private static final long DEFAULT_DELAY_MS = 200;

    /**
     * Gets a database connection using default retry settings.
     *
     * @return Active database connection.
     * @throws SQLException if connection attempts fail.
     */
    public static Connection getConnection() throws SQLException {
        return getConnection(MAX_RETRIES, DEFAULT_DELAY_MS);
    }

    /**
     * Connects to MySQL with the retry support
     *
     *  @param maxRetries Maximum number of attempts.
     *  @param delayMs Delay between attempts in milliseconds.
     *  @return Active database connection.
     *  @throws SQLException if all attempts fail.
     */
    public static Connection getConnection(int maxRetries, long delayMS)
            throws SQLException {
        if(maxRetries <= 0 || delayMS <= 0){
            throw new IllegalArgumentException(
                    "Invalid parameters passed to getConnection");
        }
        String host = System.getenv()
                .getOrDefault("DB_HOST", "localhost");

        String port = System.getenv()
                .getOrDefault("DB_PORT", "33060");

        String database = System.getenv()
                .getOrDefault("DB_NAME", "world");

        String username = System.getenv()
                .getOrDefault("DB_USER", "devops");

        String password = System.getenv()
                .getOrDefault("DB_PASSWORD", "devops");

        String url = "jdbc:mysql://" + host + ":"
                + port + "/" + database;

        System.out.println("Connecting to: " + url);

        SQLException lastException = null;

        for(int attempt = 0; attempt <= maxRetries; attempt++){
            System.out.println(
                    "Connecting to database ... Attempt "
                    + attempt + " of " + maxRetries
            );
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                System.out.println("Successfully connected to database");
                return connection;
            }catch (SQLException e){
                lastException = e;
                System.err.println("Failed to connect to database"
                + attempt + " failed  " + e.getMessage());

                if(attempt < maxRetries){
                    try {
                        Thread.sleep(delayMS);
                    }catch (InterruptedException ie){
                        Thread.currentThread().interrupt();
                        throw new SQLException(
                                "Database connection timed out. Please try again later.",
                                ie
                        );
                    }
                }
            }
        }
        throw new SQLException(
                "Failed to connect to the database after "
                + maxRetries + " attempts.",
                lastException
        );
    }

}
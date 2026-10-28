package com.napier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MainIntegrationTest {
    static CityReport cityReport;

    @BeforeAll
    static void init() {
        cityReport = new CityReport();
    }

    // Test MySQL database connection
    @Test
    void testDatabaseConnection() throws SQLException {
        try(Connection connection = DBConnection.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(2));
        }
    }

}
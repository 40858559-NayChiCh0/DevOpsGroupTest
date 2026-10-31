
package com.napier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CityReportIntegrationTest {

    static CityReport cityReport;

    @BeforeAll
    static void init() {
        cityReport = new CityReport();
    }

    /**
     * Issue 37: Integration test for cities in a country.
     */
    @Test
    void testGetCitiesByCountry() throws SQLException {

        String country = "Myanmar";

        try (Connection connection =
                     DBConnection.getConnection(10, 1000)) {

            ArrayList<City> cities =
                    cityReport.getCitiesByCountry(
                            connection, country);

            // Check list is not empty
            assertNotNull(cities);
            assertFalse(cities.isEmpty());

            // Check cities are sorted by population
            assertSortedByPopulation(cities);

            // Check all cities belong to Myanmar
            for (City city : cities) {
                assertNotNull(city);
                assertNotNull(city.getCountry());

                assertEquals(
                        country,
                        city.getCountry().getName());
            }

            // Compare report count with SQL database count
            int expected = expectedCount(connection, country);

            assertEquals(expected, cities.size());
        }
    }

    /**
     * Checks cities are ordered by population
     * from largest to smallest.
     */
    private void assertSortedByPopulation(ArrayList<City> cities) {

        for (int i = 1; i < cities.size(); i++) {

            assertTrue(
                    cities.get(i - 1).getPopulation()
                            >= cities.get(i).getPopulation(),
                    "Cities are not sorted correctly at index " + i);
        }
    }

    /**
     * Retrieves the expected number of cities
     * belonging to a country directly from MySQL.
     */
    private int expectedCount(
            Connection connection, String country) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM city
                JOIN country
                    ON city.CountryCode = country.Code
                WHERE country.Name = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, country);

            try (ResultSet resultSet = statement.executeQuery()) {

                assertTrue(resultSet.next());

                return resultSet.getInt(1);
            }
        }
    }
}

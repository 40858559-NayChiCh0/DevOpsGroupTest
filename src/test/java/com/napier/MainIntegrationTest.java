package com.napier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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

    // Test issue 34: Cities in the world
    @Test
    void testGetAllCountries() throws SQLException {
        try(Connection connection =
                DBConnection.getConnection(10,1000)){
            CityReport cityReport = new CityReport();
            ArrayList<City> cities = cityReport.getAllCities(connection);
            assertNotNull(cities);
            assertFalse(cities.isEmpty());
            for(int i = 1; i < cities.size(); i++){
                int previousPopulation =
                        cities.get(i-1).getPopulation();
                int currentPopulation =
                        cities.get(i).getPopulation();
                assertTrue(
                        previousPopulation
                        >= currentPopulation,
                        "Cities are not sorted correctly at index "
                        + i
                );
            }
        }
    }

    @Test
    void testCityDataNotNull() throws SQLException {

        try (Connection connection =
                     DBConnection.getConnection(10, 1000)) {

            CityReport cityReport = new CityReport();

            ArrayList<City> cities =
                    cityReport.getAllCities(connection);

            assertFalse(cities.isEmpty());

            for (City city : cities) {
                assertNotNull(city);
                assertNotNull(city.getName());
                assertNotNull(city.getCountry());
                assertTrue(city.getPopulation() >= 0);
            }
        }
    }
}
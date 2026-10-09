package com.napier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CapitalCityReports {

    private final Connection connection;

    public CapitalCityReports(Connection connection) {
        this.connection = connection;
    }

    // Holds a City and its Country together
    public static class CapitalRow {

        private final City city;
        private final Country country;

        public CapitalRow(City city, Country country) {
            this.city = city;
            this.country = country;
        }

        public City getCity() {
            return city;
        }

        public Country getCountry() {
            return country;
        }
    }

    // Common SQL used by all 3 reports
    private static final String BASE_SQL =
            "SELECT ci.ID AS CityID, " +
                    "ci.Name AS CityName, " +
                    "ci.CountryCode AS CountryCode, " +
                    "ci.Population AS Population, " +
                    "co.Name AS CountryName " +
                    "FROM country co " +
                    "JOIN city ci ON co.Capital = ci.ID ";

    // REPORT 1: All capital cities in the world
    public List<CapitalRow> getAllCapitalCities()
            throws SQLException {

        String sql = BASE_SQL +
                "ORDER BY ci.Population DESC";

        return executeReport(sql, null);
    }

    // REPORT 2: Capital cities in a continent
    public List<CapitalRow> getCapitalCitiesByContinent(
            String continent) throws SQLException {

        String sql = BASE_SQL +
                "WHERE co.Continent = ? " +
                "ORDER BY ci.Population DESC";

        return executeReport(sql, continent);
    }

    // REPORT 3: Capital cities in a region
    public List<CapitalRow> getCapitalCitiesByRegion(
            String region) throws SQLException {

        String sql = BASE_SQL +
                "WHERE co.Region = ? " +
                "ORDER BY ci.Population DESC";

        return executeReport(sql, region);
    }

    // Execute SQL and create City and Country objects
    private List<CapitalRow> executeReport(
            String sql, String filter) throws SQLException {

        List<CapitalRow> results = new ArrayList<>();

        try (PreparedStatement stmt =
                     connection.prepareStatement(sql)) {

            if (filter != null) {
                stmt.setString(1, filter);
            }

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    // Use City.java setters
                    City city = new City();

                    city.setId(rs.getInt("CityID"));
                    city.setName(rs.getString("CityName"));
                    city.setCountryCode(
                            rs.getString("CountryCode"));
                    city.setPopulation(
                            rs.getInt("Population"));

                    // Use Country.java setters
                    Country country = new Country();

                    country.setCode(
                            rs.getString("CountryCode"));
                    country.setName(
                            rs.getString("CountryName"));

                    results.add(new CapitalRow(city, country));
                }
            }
        }

        return results;
    }

    // Display the report in the required format
    public static void printReport(List<CapitalRow> capitals) {

        System.out.printf(
                "%-35s %-40s %15s%n",
                "Name", "Country", "Population");

        System.out.println(
                "------------------------------------------------" +
                        "-------------------------------------------");

        for (CapitalRow row : capitals) {

            System.out.printf(
                    "%-35s %-40s %,15d%n",
                    row.getCity().getName(),
                    row.getCountry().getName(),
                    row.getCity().getPopulation()
            );
        }
    }
}


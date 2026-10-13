package com.napier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // Change these values to generate reports for other locations.
        String continent = "Asia";
        String region = "Eastern Asia";
        String countryName = "Myanmar";

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null || !connection.isValid(2)) {
                System.out.println("Database connection is invalid.");
                return;
            }

            System.out.println("Database connection succeeded.");
            CityReport cityReport = new CityReport();

            // Issue 1: All cities in the world by population.
            System.out.println("\nIssue 1: All Cities in the World by Population");
            ArrayList<City> cities = cityReport.getAllCities(connection);
            cityReport.printCityReport(cities);

            // Issue 2: All cities in a continent by population.
            System.out.println("\nIssue 2: All Cities in " + continent + " by Population");
            ArrayList<City> continentCities =
                    cityReport.getCitiesByContinent(connection, continent);
            cityReport.printCityReport(continentCities);

            // Issue 3: All cities in a region by population.
            System.out.println("\nIssue 3: All Cities in " + region + " by Population");
            ArrayList<City> regionCities =
                    cityReport.getCitiesByRegion(connection, region);
            cityReport.printCityReport(regionCities);

            // Issue 4: All cities in a country by population.
            System.out.println("\nIssue 4: All Cities in " + countryName + " by Population");
            ArrayList<City> countryCities =
                    cityReport.getCitiesByCountry(connection, countryName);
            cityReport.printCityReport(countryCities);

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.err.println("Report generation failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Yu Ya Kyaw Feature -> Develop");
    }
}

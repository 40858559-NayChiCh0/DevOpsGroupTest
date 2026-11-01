package com.napier;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            if (!connection.isValid(2)) {
                System.err.println("Database connection is invalid.");
                return;
            }
            System.out.println("Database connection succeeded.");


            // Capital city reports.
            System.out.println("\nREPORT: Capital Cities Reports");
            System.out.println("\n===============================");
            CapitalCityReports capitalReports = new CapitalCityReports(connection);
            System.out.println("\nCapital Cities in the World");
            capitalReports.getAllCapitalCities();
            System.out.println("\nCapital Cities in Asia");
            capitalReports.getCapitalCitiesByContinent("Asia");
            System.out.println("\nCapital Cities in Southeast Asia");
            capitalReports.getCapitalCitiesByRegion("Southeast Asia");
            System.out.println("\nTop 10 Capital Cities in the World");
            capitalReports.getTopNCapitalCities(10);
            System.out.println("\nTop 5 Capital Cities in Asia");
            capitalReports.getTopNCapitalCitiesByContinent("Asia", 5);
            System.out.println("\nTop 3 Capital Cities in Southeast Asia");
            capitalReports.getTopNCapitalCitiesByRegion(
                    "Southeast Asia", 3);


        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
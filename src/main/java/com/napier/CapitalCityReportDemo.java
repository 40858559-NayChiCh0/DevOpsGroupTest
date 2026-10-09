
package com.napier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CapitalCityReportDemo {

    public static void main(String[] args) {

        String url = System.getenv("WORLD_DB_URL");
        String user = System.getenv("WORLD_DB_USER");
        String password = System.getenv("WORLD_DB_PASSWORD");

        if (url == null || user == null || password == null) {
            System.err.println(
                    "Please set WORLD_DB_URL, " +
                            "WORLD_DB_USER and WORLD_DB_PASSWORD."
            );
            return;
        }

        try (Connection con =
                     DriverManager.getConnection(url, user, password)) {

            CapitalCityReports reports =
                    new CapitalCityReports(con);

            // Report 1
            System.out.println("REPORT 1: World Capital Cities");
            CapitalCityReports.printReport(
                    reports.getAllCapitalCities()
            );

            // Report 2
            System.out.println("\nREPORT 2: Capitals in Asia");
            CapitalCityReports.printReport(
                    reports.getCapitalCitiesByContinent("Asia")
            );

            // Report 3
            System.out.println(
                    "\nREPORT 3: Capitals in Southeast Asia"
            );
            CapitalCityReports.printReport(
                    reports.getCapitalCitiesByRegion("Southeast Asia")
            );

        } catch (SQLException e) {
            System.err.println(
                    "Database error: " + e.getMessage()
            );
        }
    }
}

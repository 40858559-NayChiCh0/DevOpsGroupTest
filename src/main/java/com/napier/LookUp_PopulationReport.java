package com.napier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class LookUp_PopulationReport {



        /**
         * Generates a report showing the total population
         * of the world.
         *
         * @param connection Active database connection
         */
        public static void getWorldPopulation(Connection connection) {

            // SQL query to calculate the total world population
            String sql = """
                SELECT SUM(Population) AS TotalPopulation
                FROM country
                """;

            // Execute the SQL query
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                // Display report title
                System.out.println("\nWORLD POPULATION REPORT");
                System.out.println("-----------------------------------");

                // Retrieve and display world population
                if (rs.next()) {

                    long totalPopulation = rs.getLong("TotalPopulation");

                    // Display formatted population
                    System.out.printf(
                            "World Population: %,d%n",
                            totalPopulation
                    );

                } else {
                    System.out.println("No population data found.");
                }

                System.out.println("-----------------------------------");

            } catch (SQLException e) {

                // Display error if SQL execution fails
                System.err.println(
                        "Error generating world population report: "
                                + e.getMessage()
                );
            }
        }
    /**
     * Generates a report showing the total population
     * of a selected continent.
     *
     * @param continent Name of the selected continent
     * @param connection Active database connection
     */
    public static void getContinentPopulation(
            String continent, Connection connection) {

        // SQL query to calculate the continent population
        String sql = """
            SELECT Continent,
                   SUM(Population) AS TotalPopulation
            FROM country
            WHERE Continent = ?
            GROUP BY Continent
            """;

        // Execute the SQL query
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Set the selected continent
            stmt.setString(1, continent);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display report title
                System.out.println("\nCONTINENT POPULATION REPORT");
                System.out.println("-------------------------------------------");

                // Display column headings
                System.out.printf(
                        "%-20s %20s%n",
                        "Continent",
                        "Population"
                );

                System.out.println("-------------------------------------------");

                // Retrieve and display continent population
                if (rs.next()) {

                    String continentName = rs.getString("Continent");
                    long totalPopulation = rs.getLong("TotalPopulation");

                    // Display formatted population
                    System.out.printf(
                            "%-20s %,20d%n",
                            continentName,
                            totalPopulation
                    );

                } else {

                    // Display message if no matching continent is found
                    System.out.println(
                            "No population data found for continent: "
                                    + continent
                    );
                }

                System.out.println("-------------------------------------------");

            }

        } catch (SQLException e) {

            // Display error if SQL execution fails
            System.err.println(
                    "Error generating continent population report: "
                            + e.getMessage()
            );
        }
    }
    }

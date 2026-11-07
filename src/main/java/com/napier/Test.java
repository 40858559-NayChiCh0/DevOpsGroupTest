package com.napier;
import java.sql.Connection;
import java.sql.SQLException;
public class Test {


        public static void main(String[] args) {

            try (Connection connection = DBConnection.getConnection()) {

                // Generate world population report
                LookUp_PopulationReport.getWorldPopulation(connection);
                // Report 2: Population of Asia
                LookUp_PopulationReport.getContinentPopulation(
                        "Asia", connection);

            } catch (SQLException e) {
                System.err.println(
                        "Database connection error: " + e.getMessage()
                );
            }
    }
}

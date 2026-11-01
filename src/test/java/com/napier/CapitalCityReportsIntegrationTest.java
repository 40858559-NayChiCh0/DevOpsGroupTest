
package com.napier;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CapitalCityReportsIntegrationTest {

    private static Connection connection;
    private static CapitalCityReports reports;

    // Connect to the real MySQL database
    @BeforeAll
    static void setUp() throws SQLException {

        connection = DBConnection.getConnection();

        assertNotNull(connection);
        assertTrue(connection.isValid(2));

        reports = new CapitalCityReports(connection);
    }

    // Close database connection after testing
    @AfterAll
    static void tearDown() throws SQLException {

        if (connection != null) {
            connection.close();
        }
    }

    @FunctionalInterface
    interface ReportAction {
        void run() throws SQLException;
    }

    // Run the report and show real database results
    private String runReport(ReportAction action)
            throws SQLException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        try (PrintStream capture = new PrintStream(
                output, true, StandardCharsets.UTF_8)) {

            System.setOut(capture);

            // Execute the actual capital city report
            action.run();

        } finally {
            System.setOut(originalOut);
        }

        String result =
                output.toString(StandardCharsets.UTF_8);

        // Display actual database results in console
        System.out.println(result);

        return result;
    }

    // Verify columns, number of rows and population order
    private void checkReport(String output, int expectedRows) {

        assertTrue(output.contains("Name"));
        assertTrue(output.contains("Country"));
        assertTrue(output.contains("Population"));

        assertFalse(output.contains(
                "No capital cities found."));

        List<Integer> populations = new ArrayList<>();

        // Skip table header and separator
        List<String> lines =
                output.lines().skip(2).toList();

        for (String line : lines) {

            if (line.isBlank()) {
                continue;
            }

            String[] values = line.trim().split("\\s+");

            String populationText =
                    values[values.length - 1].replace(",", "");

            int population =
                    Integer.parseInt(populationText);

            populations.add(population);
        }

        // Check that rows were returned
        assertFalse(populations.isEmpty());

        // Check row count for Top N reports
        if (expectedRows > 0) {
            assertEquals(expectedRows, populations.size());
        }

        // Verify largest to smallest population
        List<Integer> sorted =
                new ArrayList<>(populations);

        sorted.sort(Collections.reverseOrder());

        assertEquals(sorted, populations);
    }

    // REPORT 1: All capital cities in the world
    @Test
    void testReport1_AllCapitalCities()
            throws SQLException {

        System.out.println(
                "\nREPORT 1: All Capital Cities in the World");

        String output = runReport(
                () -> reports.getAllCapitalCities());

        checkReport(output, -1);

        assertTrue(output.contains("Seoul"));
    }

    // REPORT 2: All capital cities in a continent
    @Test
    void testReport2_CapitalCitiesInContinent()
            throws SQLException {

        System.out.println(
                "\nREPORT 2: Capital Cities in Asia");

        String output = runReport(
                () -> reports.getCapitalCitiesByContinent(
                        "Asia"));

        checkReport(output, -1);

        assertTrue(output.contains("Seoul"));
        assertTrue(output.contains("South Korea"));
    }

    // REPORT 3: All capital cities in a region
    @Test
    void testReport3_CapitalCitiesInRegion()
            throws SQLException {

        System.out.println(
                "\nREPORT 3: Capital Cities in Southeast Asia");

        String output = runReport(
                () -> reports.getCapitalCitiesByRegion(
                        "Southeast Asia"));

        checkReport(output, -1);

        assertTrue(output.contains("Jakarta"));
        assertTrue(output.contains("Indonesia"));
    }

    // REPORT 4: Top 10 capital cities in the world
    @Test
    void testReport4_Top10WorldCapitalCities()
            throws SQLException {

        System.out.println(
                "\nREPORT 4: Top 10 Capital Cities in the World");

        String output = runReport(
                () -> reports.getTopNCapitalCities(10));

        checkReport(output, 10);

        assertTrue(output.contains("Seoul"));
    }

    // REPORT 5: Top 5 capital cities in Asia
    @Test
    void testReport5_Top5CapitalCitiesInAsia()
            throws SQLException {

        System.out.println(
                "\nREPORT 5: Top 5 Capital Cities in Asia");

        String output = runReport(
                () -> reports.getTopNCapitalCitiesByContinent(
                        "Asia", 5));

        checkReport(output, 5);

        assertTrue(output.contains("Seoul"));
    }

    // REPORT 6: Top 3 capital cities in Southeast Asia
    @Test
    void testReport6_Top3CapitalCitiesInRegion()
            throws SQLException {

        System.out.println(
                "\nREPORT 6: Top 3 Capital Cities in Southeast Asia");

        String output = runReport(
                () -> reports.getTopNCapitalCitiesByRegion(
                        "Southeast Asia", 3));

        checkReport(output, 3);

        assertTrue(output.contains("Jakarta"));
    }
}

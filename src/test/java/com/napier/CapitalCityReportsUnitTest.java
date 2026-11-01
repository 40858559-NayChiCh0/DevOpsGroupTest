
package com.napier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class CapitalCityReportsUnitTest {

    private Connection connection;
    private PreparedStatement stmt;
    private ResultSet rs;
    private CapitalCityReports reports;

    @BeforeEach
    void setUp() throws SQLException {

        // Prepare JDBC dependencies for unit testing
        connection = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

        when(connection.prepareStatement(anyString()))
                .thenReturn(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        reports = new CapitalCityReports(connection);
    }

    // REPORT 1: All capital cities in the world
    @Test
    void testReport1_AllCapitalCities() throws SQLException {

        reports.getAllCapitalCities();

        verify(connection).prepareStatement(
                argThat(sql ->
                        sql.contains("JOIN city ci ON co.Capital = ci.ID")
                                && sql.contains("ORDER BY ci.Population DESC")
                                && !sql.contains("WHERE")
                                && !sql.contains("LIMIT")
                )
        );

        verify(stmt).executeQuery();
    }

    // REPORT 2: All capital cities in a continent
    @Test
    void testReport2_CapitalCitiesByContinent()
            throws SQLException {

        reports.getCapitalCitiesByContinent("Asia");

        verify(connection).prepareStatement(
                argThat(sql ->
                        sql.contains("WHERE co.Continent = ?")
                                && sql.contains("ORDER BY ci.Population DESC")
                )
        );

        verify(stmt).setString(1, "Asia");
        verify(stmt).executeQuery();
    }

    // REPORT 3: All capital cities in a region
    @Test
    void testReport3_CapitalCitiesByRegion()
            throws SQLException {

        reports.getCapitalCitiesByRegion("Southeast Asia");

        verify(connection).prepareStatement(
                argThat(sql ->
                        sql.contains("WHERE co.Region = ?")
                                && sql.contains("ORDER BY ci.Population DESC")
                )
        );

        verify(stmt).setString(1, "Southeast Asia");
        verify(stmt).executeQuery();
    }

    // REPORT 4: Top N capital cities in the world
    @Test
    void testReport4_TopNCapitalCities()
            throws SQLException {

        reports.getTopNCapitalCities(10);

        verify(connection).prepareStatement(
                argThat(sql ->
                        sql.contains("ORDER BY ci.Population DESC")
                                && sql.contains("LIMIT ?")
                                && !sql.contains("WHERE")
                )
        );

        verify(stmt).setInt(1, 10);
        verify(stmt).executeQuery();
    }

    // REPORT 5: Top N capital cities in a continent
    @Test
    void testReport5_TopNCapitalCitiesByContinent()
            throws SQLException {

        reports.getTopNCapitalCitiesByContinent("Asia", 5);

        verify(connection).prepareStatement(
                argThat(sql ->
                        sql.contains("WHERE co.Continent = ?")
                                && sql.contains("ORDER BY ci.Population DESC")
                                && sql.contains("LIMIT ?")
                )
        );

        verify(stmt).setString(1, "Asia");
        verify(stmt).setInt(2, 5);
        verify(stmt).executeQuery();
    }

    // REPORT 6: Top N capital cities in a region
    @Test
    void testReport6_TopNCapitalCitiesByRegion()
            throws SQLException {

        reports.getTopNCapitalCitiesByRegion(
                "Southeast Asia", 3);

        verify(connection).prepareStatement(
                argThat(sql ->
                        sql.contains("WHERE co.Region = ?")
                                && sql.contains("ORDER BY ci.Population DESC")
                                && sql.contains("LIMIT ?")
                )
        );

        verify(stmt).setString(1, "Southeast Asia");
        verify(stmt).setInt(2, 3);
        verify(stmt).executeQuery();
    }

    // EXTRA TEST: Invalid Top N value
    @Test
    void testInvalidTopN() throws SQLException {

        reports.getTopNCapitalCities(0);

        verify(connection, never())
                .prepareStatement(anyString());
    }
}

package com.napier;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    static void main(String[] args) {

        // Change these values to generate reports for other locations.
        String continent = "Asia";
        String region = "Eastern Asia";
        String countryName = "Myanmar";
        String district = "Sagaing";
        int n = 10;

        try (Connection connection =
                     DBConnection.getConnection()) {

            if (connection.isValid(2)) {
                System.out.println(
                        "Database connection succeeded.");
                CityReport cityReport = new CityReport();


                // Issue 34: All cities in the world by population.
                ArrayList<City> cities =
                        cityReport.getAllCities(connection);

                cityReport.printCityReport(
                        cities,
                        "ALL CITIES IN THE WORLD BY POPULATION");

                // Issue 35: All cities in a continent(Asia) by population.
                ArrayList<City> continentCities =
                        cityReport.getCitiesByContinent(
                                connection, continent);

                cityReport.printCityReport(
                        continentCities,
                        "ALL CITIES IN " + continent.toUpperCase()
                                + " BY POPULATION");

                // Issue 36: All cities in a region by population.
                ArrayList<City> regionCities =
                        cityReport.getCitiesByRegion(
                                connection, region);

                cityReport.printCityReport(
                        regionCities,
                        "ALL CITIES IN " + region.toUpperCase()
                                + " BY POPULATION");

                // Issue 37: All cities in a country by population
                ArrayList<City> countryCities =
                        cityReport.getCitiesByCountry(
                                connection, countryName);
                cityReport.printCityReport(countryCities,
                        "ALL CITIES IN " + countryName.toUpperCase()
                                + "BY POPULATION");

                // Issue 38: All cities in a district by population
                ArrayList<City> districtCities = cityReport.getCitiesByDistrict(
                        connection, district);
                cityReport.printCityReport(districtCities,
                        "ALL CITIES IN " + district.toUpperCase()
                                +"BY POPULATION"
                );

                //Issue 39: Top N populated Cities in the World
                ArrayList<City> topNCities = cityReport.getTopNCitiesInWorld(connection,n);
                cityReport.printCityReport(
                        topNCities, "TOP " + n + " POPULATED CITIES IN THE WORLD"
                );
                writeTopCitiesReport(topNCities, n);

                // Issue 40: Top N populated Cities in a continent
                ArrayList<City> topNCitiesByContinent = cityReport.getTopNCitiesByContinent(connection, continent, n);
                cityReport.printCityReport(
                        topNCitiesByContinent, "TOP " + n + " POPULATED CITIES IN " + continent.toUpperCase()
                );

                addCity("Test City", "MMR","Sagaing",50000);
            } else {
                System.out.println(
                        "Database connection is invalid.");
            }

        } catch (SQLException e) {

            System.err.println(
                    "Database connection failed: "
                            + e.getMessage());

            e.printStackTrace();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write the Markdown city report", e);
        }
    }

    private static void writeTopCitiesReport(List<City> cities, int limit) throws IOException {
        Path reportDirectory = Path.of("reports");
        Files.createDirectories(reportDirectory);

        StringBuilder markdown = new StringBuilder();
        markdown.append("# Top ").append(limit).append(" populated cities in the world\n\n");

        if (cities.isEmpty()) {
            markdown.append("No cities found.\n");
        } else {
            markdown.append("| Rank | City | Country | District | Population |\n")
                    .append("| ---: | --- | --- | --- | ---: |\n");

            int rank = 1;
            for (City city : cities) {
                Country country = city.getCountry();
                markdown.append("| ").append(rank++)
                        .append(" | ").append(markdownCell(city.getName()))
                        .append(" | ").append(markdownCell(country == null ? null : country.getName()))
                        .append(" | ").append(markdownCell(city.getDistrict()))
                        .append(" | ").append(String.format(Locale.ROOT, "%,d", city.getPopulation()))
                        .append(" |\n");
            }
        }

        Path report = reportDirectory.resolve("README.md");
        Files.writeString(report, markdown);
        System.out.println("Markdown report written to " + report.toAbsolutePath());
    }

    private static String markdownCell(String value) {
        if (value == null || value.isBlank()) {
            return "Unknown";
        }

        return value.replace("&", "&amp;")
                .replace("\\", "&#92;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("|", "&#124;")
                .replace("\r", " ")
                .replace("\n", " ")
                .strip();
    }

    public static void addCity(String cityName, String countryCode,
                               String district, int population){
        String sql = """
                INSERT INTO city (Name, CountryCode, District, Population)
                            VALUES (?, ?, ?, ?)
                """;
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,cityName);
            statement.setString(2,countryCode);
            statement.setString(3,district);
            statement.setInt(4,population);

            int rowsAffected = statement.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("City added successfully.");
            }else{
                System.out.println("Failed to insert city");
            }
        }catch (SQLException e){
            System.out.println("Error adding city: "+e.getMessage());
        }
    }
}

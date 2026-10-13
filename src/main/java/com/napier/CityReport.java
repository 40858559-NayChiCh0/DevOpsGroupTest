package com.napier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CityReport {

    /**
     * Gets all cities in the world ordered by population.
     *
     * @param connection Database connection.
     * @return List of cities.
     */
    public ArrayList<City> getAllCities(Connection connection) {

        ArrayList<City> cities = new ArrayList<>();

        String sql = """
                SELECT city.ID,
                       city.Name,
                       city.CountryCode,
                       country.Name AS CountryName,
                       city.District,
                       city.Population
                FROM city
                JOIN country
                    ON city.CountryCode = country.Code
                ORDER BY city.Population DESC, city.ID ASC
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                City city = new City();

                city.setId(resultSet.getInt("ID"));
                city.setName(resultSet.getString("Name"));
                city.setCountryCode(
                        resultSet.getString("CountryCode"));
                city.setDistrict(
                        resultSet.getString("District"));
                city.setPopulation(
                        resultSet.getInt("Population"));

                // Create a Country object
                Country country = new Country();

                country.setCode(
                        resultSet.getString("CountryCode"));
                country.setName(
                        resultSet.getString("CountryName"));

                // Associate the country with the city
                city.setCountry(country);

                cities.add(city);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve city report.", e);
        }

        return cities;
    }


    /**
     * Prints all cities in a formatted table.
     *
     * @param cities List of cities.
     */
    public void printCityReport(ArrayList<City> cities) {

        if (cities == null || cities.isEmpty()) {
            System.out.println("No cities found.");
            return;
        }

        System.out.println(
                "\n========== ALL CITIES IN THE WORLD ==========");

        System.out.printf(
                "%-25s %-25s %-25s %15s%n",
                "Name", "Country", "District", "Population");

        System.out.println("-".repeat(95));

        for (City city : cities) {

            System.out.printf(
                    "%-25.25s %-25.25s %-25.25s %,15d%n",
                    city.getName(),
                    city.getCountry().getName(),
                    city.getDistrict(),
                    city.getPopulation());
        }

        System.out.println("-".repeat(95));
        System.out.println("Total cities: " + cities.size());
    }



    /**
     * Retrieves all cities in a selected continent,
     * ordered by population from largest to smallest.
     *
     * @param connection Active database connection.
     * @param continent Name of the selected continent.
     * @return List of cities in the selected continent.
     */
    public ArrayList<City> getCitiesByContinent(
            Connection connection, String continent) {

        ArrayList<City> cities = new ArrayList<>();

        String sql = """
            SELECT city.ID,
                   city.Name,
                   city.CountryCode,
                   country.Name AS CountryName,
                   city.District,
                   city.Population
            FROM city
            JOIN country
                ON city.CountryCode = country.Code
            WHERE country.Continent = ?
            ORDER BY city.Population DESC, city.ID ASC
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, continent);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    City city = new City();

                    city.setId(resultSet.getInt("ID"));
                    city.setName(resultSet.getString("Name"));
                    city.setCountryCode(
                            resultSet.getString("CountryCode"));
                    city.setDistrict(
                            resultSet.getString("District"));
                    city.setPopulation(
                            resultSet.getInt("Population"));

                    Country country = new Country();

                    country.setCode(
                            resultSet.getString("CountryCode"));
                    country.setName(
                            resultSet.getString("CountryName"));

                    city.setCountry(country);

                    cities.add(city);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve cities for continent: "
                            + continent, e);
        }

        return cities;
    }



    /**
     * Issue 3: Gets all cities in a selected region,
     * ordered by population from largest to smallest.
     *
     * @param connection Database connection.
     * @param region Selected region.
     * @return List of cities in the selected region.
     */
    public ArrayList<City> getCitiesByRegion(
            Connection connection, String region) {

        ArrayList<City> cities = new ArrayList<>();

        String sql = """
            SELECT city.ID,
                   city.Name,
                   city.CountryCode,
                   country.Name AS CountryName,
                   city.District,
                   city.Population
            FROM city
            JOIN country
                ON city.CountryCode = country.Code
            WHERE country.Region = ?
            ORDER BY city.Population DESC, city.ID ASC
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, region);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    City city = new City();

                    city.setId(resultSet.getInt("ID"));
                    city.setName(resultSet.getString("Name"));
                    city.setCountryCode(
                            resultSet.getString("CountryCode"));
                    city.setDistrict(
                            resultSet.getString("District"));
                    city.setPopulation(
                            resultSet.getInt("Population"));

                    Country country = new Country();

                    country.setCode(
                            resultSet.getString("CountryCode"));
                    country.setName(
                            resultSet.getString("CountryName"));

                    city.setCountry(country);
                    cities.add(city);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve cities for region: "
                            + region, e);
        }

        return cities;
    }



    /**
     * Retrieves all available regions from the database.
     *
     * @param connection Database connection.
     * @return List of region names.
     */
    public ArrayList<String> getAllRegions(Connection connection) {

        ArrayList<String> regions = new ArrayList<>();

        String sql = """
            SELECT DISTINCT Region
            FROM country
            WHERE Region IS NOT NULL
              AND Region <> ''
            ORDER BY Region ASC
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                regions.add(resultSet.getString("Region"));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve regions.", e);
        }

        return regions;
    }



    /**
     * Issue 4: Gets all cities in a selected country,
     * ordered by population from largest to smallest.
     *
     * @param connection Database connection.
     * @param countryName Selected country name.
     * @return List of cities in the selected country.
     */
    public ArrayList<City> getCitiesByCountry(
            Connection connection, String countryName) {

        ArrayList<City> cities = new ArrayList<>();

        String sql = """
            SELECT city.ID,
                   city.Name,
                   city.CountryCode,
                   country.Name AS CountryName,
                   city.District,
                   city.Population
            FROM city
            JOIN country
                ON city.CountryCode = country.Code
            WHERE country.Name = ?
            ORDER BY city.Population DESC, city.ID ASC
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, countryName);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    City city = new City();

                    city.setId(resultSet.getInt("ID"));
                    city.setName(resultSet.getString("Name"));
                    city.setCountryCode(
                            resultSet.getString("CountryCode"));
                    city.setDistrict(
                            resultSet.getString("District"));
                    city.setPopulation(
                            resultSet.getInt("Population"));

                    Country country = new Country();

                    country.setCode(
                            resultSet.getString("CountryCode"));
                    country.setName(
                            resultSet.getString("CountryName"));

                    city.setCountry(country);
                    cities.add(city);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve cities for country: "
                            + countryName, e);
        }

        return cities;
    }


    /**
     * Issue 4: Retrieves all countries from the database,
     * ordered alphabetically by country name.
     *
     * @param connection Database connection.
     * @return List of country names.
     */
    public ArrayList<String> getAllCountries(Connection connection) {

        ArrayList<String> countries = new ArrayList<>();

        String sql = """
            SELECT Name
            FROM country
            ORDER BY Name ASC
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                countries.add(resultSet.getString("Name"));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to retrieve countries.", e);
        }

        return countries;
    }

}

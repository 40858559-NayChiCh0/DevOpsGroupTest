package com.napier;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        System.out.println(
                "Report 1: All the countries in the world organised by largest population to smallest.");

        CountryReports.getAllCountries();

        System.out.println(
                "Report 2: All the countries in a continent organised by largest population to smallest. (Continent of the Asia )");
        CountryReports.getCountriesByContinent("Asia");


        System.out.println(
                "Report 3: All the countries in a region organised by largest population to smallest. (Region of the Southeast Asia)");
        CountryReports.getCountriesByRegion("Southeast Asia");
    }


}
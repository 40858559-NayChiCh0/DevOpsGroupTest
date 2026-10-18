package com.napier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    City city = new City();

    @Test
    void getId() {
        city.setId(1);
        assertEquals(1, city.getId());
    }

    @Test
    void setId() {
        city.setId(2);
        assertEquals(2, city.getId());
    }

    @Test
    void getName() {
        city.setName("Yangon");
        assertEquals("Yangon", city.getName());
    }

    @Test
    void setName() {
        city.setName("Mandalay");
        assertEquals("Mandalay", city.getName());
    }
}
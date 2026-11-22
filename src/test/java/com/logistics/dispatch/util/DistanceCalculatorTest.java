package com.logistics.dispatch.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    private final DistanceCalculator calculator =
            new DistanceCalculator();

    @Test
    void shouldReturnZeroForSameCoordinates() {
        double distance = calculator.calculateDistance(
                13.0827, 80.2707,
                13.0827, 80.2707
        );

        assertEquals(0.0, distance, 0.0001);
    }

    @Test
    void shouldCalculateDistanceBetweenCoordinates() {
        double distance = calculator.calculateDistance(
                0.0, 0.0,
                0.0, 1.0
        );

        assertEquals(111.19, distance, 0.1);
    }

    @Test
    void shouldReturnPositiveDistance() {
        double distance = calculator.calculateDistance(
                13.0827, 80.2707,
                13.0674, 80.2376
        );

        assertTrue(distance > 0);
    }
}

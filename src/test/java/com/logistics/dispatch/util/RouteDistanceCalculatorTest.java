package com.logistics.dispatch.util;


import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteDistanceCalculatorTest {

    private RouteDistanceCalculator routeCalculator;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        routeCalculator = new RouteDistanceCalculator(
                new DistanceCalculator()
        );

        vehicle = new Vehicle();
        vehicle.setCurrentLatitude(0.0);
        vehicle.setCurrentLongitude(0.0);
    }

    @Test
    void shouldReturnZeroForEmptyRoute() {
        double distance = routeCalculator.calculateTotalDistance(
                vehicle, List.of()
        );

        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void shouldCalculateDistanceForOneDelivery() {
        DeliveryOrder order = createOrder(0.0, 1.0);

        double distance = routeCalculator.calculateTotalDistance(
                vehicle, List.of(order)
        );

        assertEquals(111.19, distance, 0.1);
    }

    @Test
    void shouldCalculateDistanceForMultipleDeliveries() {
        DeliveryOrder first = createOrder(0.0, 1.0);
        DeliveryOrder second = createOrder(0.0, 2.0);

        double distance = routeCalculator.calculateTotalDistance(
                vehicle, List.of(first, second)
        );

        assertEquals(222.39, distance, 0.2);
    }

    @Test
    void shouldCalculateDistanceAfterInsertingAtBeginning() {
        DeliveryOrder existing = createOrder(0.0, 2.0);
        DeliveryOrder newOrder = createOrder(0.0, 1.0);

        double distance = routeCalculator.calculateDistanceAfterInsertion(
                vehicle, List.of(existing), newOrder, 0
        );

        assertEquals(222.39, distance, 0.2);
    }

    @Test
    void shouldCalculateDistanceAfterInsertingAtEnd() {
        DeliveryOrder existing = createOrder(0.0, 1.0);
        DeliveryOrder newOrder = createOrder(0.0, 2.0);

        double distance = routeCalculator.calculateDistanceAfterInsertion(
                vehicle, List.of(existing), newOrder, 1
        );

        assertEquals(222.39, distance, 0.2);
    }

    @Test
    void shouldRejectInvalidInsertionPosition() {
        DeliveryOrder newOrder = createOrder(0.0, 1.0);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> routeCalculator.calculateDistanceAfterInsertion(
                        vehicle, List.of(), newOrder, 1
                )
        );
    }

    private DeliveryOrder createOrder(
            double latitude,
            double longitude) {

        DeliveryOrder order = new DeliveryOrder();

        order.setLatitude(latitude);
        order.setLongitude(longitude);
        order.setPackageWeight(new BigDecimal("10"));

        return order;
    }
}

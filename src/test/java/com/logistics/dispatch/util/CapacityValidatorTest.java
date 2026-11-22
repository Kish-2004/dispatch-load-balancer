package com.logistics.dispatch.util;


import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CapacityValidatorTest {

    @Test
    void shouldAllowOrderWhenCapacityIsAvailable() {
        Vehicle vehicle = createVehicle("100");
        DeliveryOrder order = createOrder("20");

        assertTrue(CapacityValidator.canFit(
                vehicle, new BigDecimal("50"), order));
    }

    @Test
    void shouldAllowOrderWhenCapacityIsExactlyReached() {
        Vehicle vehicle = createVehicle("100");
        DeliveryOrder order = createOrder("50");

        assertTrue(CapacityValidator.canFit(
                vehicle, new BigDecimal("50"), order));
    }

    @Test
    void shouldRejectOrderWhenCapacityWouldBeExceeded() {
        Vehicle vehicle = createVehicle("100");
        DeliveryOrder order = createOrder("60");

        assertFalse(CapacityValidator.canFit(
                vehicle, new BigDecimal("50"), order));
    }

    @Test
    void shouldRejectOrderHeavierThanVehicleCapacity() {
        Vehicle vehicle = createVehicle("50");
        DeliveryOrder order = createOrder("60");

        assertTrue(CapacityValidator.exceedsCapacity(vehicle, order));
    }

    @Test
    void shouldAcceptOrderWithinVehicleCapacity() {
        Vehicle vehicle = createVehicle("100");
        DeliveryOrder order = createOrder("40");

        assertFalse(CapacityValidator.exceedsCapacity(vehicle, order));
    }

    @Test
    void shouldReturnFalseWhenVehicleIsNull() {
        assertFalse(CapacityValidator.canFit(
                null, BigDecimal.ZERO, createOrder("10")));
    }

    private Vehicle createVehicle(String capacity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setCapacity(new BigDecimal(capacity));
        return vehicle;
    }

    private DeliveryOrder createOrder(String weight) {
        DeliveryOrder order = new DeliveryOrder();
        order.setPackageWeight(new BigDecimal(weight));
        return order;
    }
}

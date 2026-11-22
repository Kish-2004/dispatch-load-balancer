
package com.logistics.dispatch.service;

import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehicleRoute {

    private final Vehicle vehicle;

    private final List<DeliveryOrder> assignedOrders =
            new ArrayList<>();

    private BigDecimal totalLoad = BigDecimal.ZERO;

    private double totalDistanceKm;

    public VehicleRoute(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException(
                    "Vehicle cannot be null");
        }

        this.vehicle = vehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public List<DeliveryOrder> getAssignedOrders() {
        return Collections.unmodifiableList(assignedOrders);
    }

    public BigDecimal getTotalLoad() {
        return totalLoad;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void addOrder(
            DeliveryOrder order,
            int position,
            double newDistanceKm) {

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order cannot be null");
        }

        if (position < 0 || position > assignedOrders.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid insertion position: " + position);
        }

        if (!Double.isFinite(newDistanceKm) || newDistanceKm < 0) {
            throw new IllegalArgumentException(
                    "Distance must be a finite non-negative value");
        }

        if (order.getPackageWeight() == null
                || order.getPackageWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Order weight must be positive");
        }

        assignedOrders.add(position, order);

        totalLoad = totalLoad.add(order.getPackageWeight());

        totalDistanceKm = newDistanceKm;
    }

    public void updateDistance(double distanceKm) {

        if (!Double.isFinite(distanceKm) || distanceKm < 0) {
            throw new IllegalArgumentException(
                    "Distance must be a finite non-negative value");
        }

        this.totalDistanceKm = distanceKm;
    }
}

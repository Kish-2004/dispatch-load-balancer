package com.logistics.dispatch.util;

import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteDistanceCalculator {

    private final DistanceCalculator distanceCalculator;

    public RouteDistanceCalculator(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    /**
     * Calculates total distance from the vehicle's starting location
     * through all assigned delivery locations.
     *
     * No return-to-depot distance is included.
     */
    public double calculateTotalDistance(
            Vehicle vehicle,
            List<DeliveryOrder> orders) {

        if (vehicle == null || orders == null) {
            throw new IllegalArgumentException(
                    "Vehicle and orders cannot be null");
        }

        double totalDistance = 0.0;

        double previousLatitude = vehicle.getCurrentLatitude();
        double previousLongitude = vehicle.getCurrentLongitude();

        for (DeliveryOrder order : orders) {

            totalDistance += distanceCalculator.calculateDistance(
                    previousLatitude,
                    previousLongitude,
                    order.getLatitude(),
                    order.getLongitude());

            previousLatitude = order.getLatitude();
            previousLongitude = order.getLongitude();
        }

        return totalDistance;
    }

    public double calculateDistanceAfterInsertion(
            Vehicle vehicle,
            List<DeliveryOrder> currentOrders,
            DeliveryOrder newOrder,
            int position,
            double currentDistance) {

        if (vehicle == null || currentOrders == null || newOrder == null) {
            throw new IllegalArgumentException(
                    "Vehicle, orders and new order cannot be null");
        }

        if (position < 0 || position > currentOrders.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid insertion position: " + position);
        }

        if (!Double.isFinite(currentDistance) || currentDistance < 0) {
            throw new IllegalArgumentException(
                    "Current distance must be finite and non-negative");
        }

        double previousLatitude;
        double previousLongitude;

        if (position == 0) {
            previousLatitude = vehicle.getCurrentLatitude();
            previousLongitude = vehicle.getCurrentLongitude();
        } else {
            DeliveryOrder previousOrder = currentOrders.get(position - 1);
            previousLatitude = previousOrder.getLatitude();
            previousLongitude = previousOrder.getLongitude();
        }

        double additionalDistance = distanceCalculator.calculateDistance(
                previousLatitude,
                previousLongitude,
                newOrder.getLatitude(),
                newOrder.getLongitude());

        if (position < currentOrders.size()) {

            DeliveryOrder nextOrder = currentOrders.get(position);

            double oldDistance = distanceCalculator.calculateDistance(
                    previousLatitude,
                    previousLongitude,
                    nextOrder.getLatitude(),
                    nextOrder.getLongitude());

            double newDistance = distanceCalculator.calculateDistance(
                    newOrder.getLatitude(),
                    newOrder.getLongitude(),
                    nextOrder.getLatitude(),
                    nextOrder.getLongitude());

            additionalDistance += newDistance - oldDistance;
        }

        return currentDistance + additionalDistance;
    }

    /**
     * Calculates the new route distance if an order is inserted
     * at the specified position.
     *
     * The original list is not modified.
     */
    public double calculateDistanceAfterInsertion(
            Vehicle vehicle,
            List<DeliveryOrder> currentOrders,
            DeliveryOrder newOrder,
            int position) {

        if (vehicle == null || currentOrders == null || newOrder == null) {
            throw new IllegalArgumentException(
                    "Vehicle, orders and new order cannot be null");
        }

        if (position < 0 || position > currentOrders.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid insertion position: " + position);
        }

        double previousLatitude;
        double previousLongitude;

        if (position == 0) {
            previousLatitude = vehicle.getCurrentLatitude();
            previousLongitude = vehicle.getCurrentLongitude();
        } else {
            DeliveryOrder previousOrder = currentOrders.get(position - 1);
            previousLatitude = previousOrder.getLatitude();
            previousLongitude = previousOrder.getLongitude();
        }

        double additionalDistance = distanceCalculator.calculateDistance(
                previousLatitude,
                previousLongitude,
                newOrder.getLatitude(),
                newOrder.getLongitude());

        if (position < currentOrders.size()) {
            DeliveryOrder nextOrder = currentOrders.get(position);

            double oldDistance = distanceCalculator.calculateDistance(
                    previousLatitude,
                    previousLongitude,
                    nextOrder.getLatitude(),
                    nextOrder.getLongitude());

            double newDistance = distanceCalculator.calculateDistance(
                    newOrder.getLatitude(),
                    newOrder.getLongitude(),
                    nextOrder.getLatitude(),
                    nextOrder.getLongitude());

            additionalDistance += newDistance - oldDistance;
        }

        double currentDistance = calculateTotalDistance(vehicle, currentOrders);

        return currentDistance + additionalDistance;
    }
}

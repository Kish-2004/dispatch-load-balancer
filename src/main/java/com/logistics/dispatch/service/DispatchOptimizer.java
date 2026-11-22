
package com.logistics.dispatch.service;

import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;
import com.logistics.dispatch.util.CapacityValidator;
import com.logistics.dispatch.util.OrderPriorityComparator;
import com.logistics.dispatch.util.RouteDistanceCalculator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DispatchOptimizer {

        private final RouteDistanceCalculator routeDistanceCalculator;

        public DispatchOptimizer(
                        RouteDistanceCalculator routeDistanceCalculator) {
                this.routeDistanceCalculator = routeDistanceCalculator;
        }

        public List<VehicleRoute> optimize(
                        List<DeliveryOrder> orders,
                        List<Vehicle> vehicles) {

                if (orders == null || vehicles == null) {
                        throw new IllegalArgumentException(
                                        "Orders and vehicles cannot be null");
                }

                List<DeliveryOrder> sortedOrders = new ArrayList<>(orders);

                sortedOrders.sort(new OrderPriorityComparator());

                // Sort vehicles before assignment for deterministic tie-breaking.
                List<Vehicle> sortedVehicles = new ArrayList<>(vehicles);

                sortedVehicles.sort(
                                Comparator.comparing(Vehicle::getVehicleId));

                List<VehicleRoute> routes = sortedVehicles.stream()
                                .map(VehicleRoute::new)
                                .toList();

                List<VehicleRoute> mutableRoutes = new ArrayList<>(routes);

                for (DeliveryOrder order : sortedOrders) {

                        VehicleRoute bestRoute = null;
                        int bestPosition = -1;

                        double minimumAdditionalDistance = Double.MAX_VALUE;

                        double bestNewDistance = 0;

                        for (VehicleRoute route : mutableRoutes) {

                                Vehicle vehicle = route.getVehicle();

                                if (!CapacityValidator.canFit(
                                                vehicle,
                                                route.getTotalLoad(),
                                                order)) {
                                        continue;
                                }

                                for (int position = 0; position <= route.getAssignedOrders().size(); position++) {

                                        double newDistance = routeDistanceCalculator
                                                        .calculateDistanceAfterInsertion(
                                                                        vehicle,
                                                                        route.getAssignedOrders(),
                                                                        order,
                                                                        position,
                                                                        route.getTotalDistanceKm());

                                        double additionalDistance = newDistance - route.getTotalDistanceKm();

                                        if (additionalDistance < minimumAdditionalDistance) {

                                                minimumAdditionalDistance = additionalDistance;

                                                bestRoute = route;
                                                bestPosition = position;
                                                bestNewDistance = newDistance;
                                        }
                                }
                        }

                        if (bestRoute != null) {

                                bestRoute.addOrder(
                                                order,
                                                bestPosition,
                                                bestNewDistance);
                        }
                }

                return mutableRoutes;
        }
}

package com.logistics.dispatch.service;


import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Priority;
import com.logistics.dispatch.entity.Vehicle;
import com.logistics.dispatch.util.DistanceCalculator;
import com.logistics.dispatch.util.RouteDistanceCalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DispatchOptimizerTest {

    private DispatchOptimizer optimizer;

    @BeforeEach
    void setUp() {
        DistanceCalculator distanceCalculator =
                new DistanceCalculator();

        RouteDistanceCalculator routeCalculator =
                new RouteDistanceCalculator(distanceCalculator);

        optimizer = new DispatchOptimizer(routeCalculator);
    }

    private Vehicle createVehicle(
            String id, String capacity,
            double latitude, double longitude) {

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(id);
        vehicle.setCapacity(new BigDecimal(capacity));
        vehicle.setCurrentLatitude(latitude);
        vehicle.setCurrentLongitude(longitude);
        vehicle.setCurrentAddress("Starting location");

        return vehicle;
    }

    private DeliveryOrder createOrder(
            String id,
            Priority priority,
            String weight,
            double latitude,
            double longitude) {

        DeliveryOrder order = new DeliveryOrder();
        order.setOrderId(id);
        order.setPriority(priority);
        order.setPackageWeight(new BigDecimal(weight));
        order.setLatitude(latitude);
        order.setLongitude(longitude);
        order.setAddress("Delivery " + id);

        return order;
    }

    @Test
    void shouldReturnEmptyRoutesWhenNoOrdersExist() {

        Vehicle vehicle = createVehicle(
                "V1", "100", 13.0827, 80.2707);

        List<VehicleRoute> routes =
                optimizer.optimize(List.of(), List.of(vehicle));

        assertEquals(1, routes.size());
        assertTrue(routes.get(0).getAssignedOrders().isEmpty());
        assertEquals(BigDecimal.ZERO, routes.get(0).getTotalLoad());
    }

    @Test
    void shouldAssignOrderToAvailableVehicle() {

        Vehicle vehicle = createVehicle(
                "V1", "100", 13.0827, 80.2707);

        DeliveryOrder order = createOrder(
                "O1", Priority.HIGH, "30",
                13.0900, 80.2800);

        List<VehicleRoute> routes =
                optimizer.optimize(List.of(order), List.of(vehicle));

        assertEquals(1, routes.size());
        assertEquals(1, routes.get(0).getAssignedOrders().size());
        assertEquals("O1",
                routes.get(0).getAssignedOrders().get(0).getOrderId());
        assertEquals(new BigDecimal("30"),
                routes.get(0).getTotalLoad());
    }

    @Test
    void shouldNeverExceedVehicleCapacity() {

        Vehicle vehicle = createVehicle(
                "V1", "50", 13.0827, 80.2707);

        DeliveryOrder order1 = createOrder(
                "O1", Priority.HIGH, "30",
                13.0900, 80.2800);

        DeliveryOrder order2 = createOrder(
                "O2", Priority.MEDIUM, "30",
                13.1000, 80.2900);

        List<VehicleRoute> routes =
                optimizer.optimize(
                        List.of(order1, order2),
                        List.of(vehicle));

        assertEquals(new BigDecimal("30"),
                routes.get(0).getTotalLoad());

        assertEquals(1, routes.get(0).getAssignedOrders().size());
    }

    @Test
    void shouldProcessHighPriorityBeforeLowPriority() {

        Vehicle vehicle = createVehicle(
                "V1", "50", 13.0827, 80.2707);

        DeliveryOrder low = createOrder(
                "O1", Priority.LOW, "30",
                13.0900, 80.2800);

        DeliveryOrder high = createOrder(
                "O2", Priority.HIGH, "30",
                13.1000, 80.2900);

        List<VehicleRoute> routes =
                optimizer.optimize(
                        List.of(low, high),
                        List.of(vehicle));

        assertEquals(1, routes.get(0).getAssignedOrders().size());

        assertEquals(
                "O2",
                routes.get(0).getAssignedOrders().get(0).getOrderId()
        );
    }

    @Test
    void shouldLeaveOrderUnassignedWhenNoVehicleCanFitIt() {

        Vehicle vehicle = createVehicle(
                "V1", "20", 13.0827, 80.2707);

        DeliveryOrder order = createOrder(
                "O1", Priority.HIGH, "50",
                13.0900, 80.2800);

        List<VehicleRoute> routes =
                optimizer.optimize(List.of(order), List.of(vehicle));

        assertTrue(routes.get(0).getAssignedOrders().isEmpty());
        assertEquals(BigDecimal.ZERO, routes.get(0).getTotalLoad());
    }

    @Test
    void shouldDistributeOrdersAcrossMultipleVehicles() {

        Vehicle vehicle1 = createVehicle(
                "V1", "30", 13.0827, 80.2707);

        Vehicle vehicle2 = createVehicle(
                "V2", "30", 13.0827, 80.2707);

        DeliveryOrder order1 = createOrder(
                "O1", Priority.HIGH, "30",
                13.0900, 80.2800);

        DeliveryOrder order2 = createOrder(
                "O2", Priority.MEDIUM, "30",
                13.1000, 80.2900);

        List<VehicleRoute> routes =
                optimizer.optimize(
                        List.of(order1, order2),
                        List.of(vehicle1, vehicle2));

        int assignedCount = routes.stream()
                .mapToInt(route -> route.getAssignedOrders().size())
                .sum();

        assertEquals(2, assignedCount);

        assertEquals(
                new BigDecimal("30"),
                routes.get(0).getTotalLoad()
        );

        assertEquals(
                new BigDecimal("30"),
                routes.get(1).getTotalLoad()
        );
    }

    @Test
    void shouldReturnEmptyRoutesWhenNoVehiclesExist() {

        DeliveryOrder order = createOrder(
                "O1", Priority.HIGH, "10",
                13.0900, 80.2800);

        List<VehicleRoute> routes =
                optimizer.optimize(List.of(order), List.of());

        assertTrue(routes.isEmpty());
    }

    @Test
    void shouldRejectNullOrders() {

        assertThrows(
                IllegalArgumentException.class,
                () -> optimizer.optimize(null, List.of())
        );
    }

    @Test
    void shouldRejectNullVehicles() {

        assertThrows(
                IllegalArgumentException.class,
                () -> optimizer.optimize(List.of(), null)
        );
    }

    @Test
    void shouldProcessMediumPriorityBeforeLowPriority() {

        Vehicle vehicle = createVehicle(
                "V1", "30", 13.0827, 80.2707);

        DeliveryOrder low = createOrder(
                "O1", Priority.LOW, "20",
                13.0900, 80.2800);

        DeliveryOrder medium = createOrder(
                "O2", Priority.MEDIUM, "30",
                13.1000, 80.2900);

        List<VehicleRoute> routes =
                optimizer.optimize(
                        List.of(low, medium),
                        List.of(vehicle));

        assertEquals(1, routes.get(0).getAssignedOrders().size());

        assertEquals(
                "O2",
                routes.get(0).getAssignedOrders().get(0).getOrderId());
    }

    @Test
    void shouldPreferHeavierOrderWhenPriorityIsSame() {

        Vehicle vehicle = createVehicle(
                "V1", "30", 13.0827, 80.2707);

        DeliveryOrder lighter = createOrder(
                "O1", Priority.HIGH, "20",
                13.0900, 80.2800);

        DeliveryOrder heavier = createOrder(
                "O2", Priority.HIGH, "30",
                13.1000, 80.2900);

        List<VehicleRoute> routes =
                optimizer.optimize(
                        List.of(lighter, heavier),
                        List.of(vehicle));

        assertEquals(1, routes.get(0).getAssignedOrders().size());

        assertEquals(
                "O2",
                routes.get(0).getAssignedOrders().get(0).getOrderId());

        assertEquals(
                new BigDecimal("30"),
                routes.get(0).getTotalLoad());
    }

    @Test
    void shouldSelectVehicleWithLowestIdWhenDistancesAreEqual() {

        Vehicle vehicle2 = createVehicle(
                "V2", "50", 13.0827, 80.2707);

        Vehicle vehicle1 = createVehicle(
                "V1", "50", 13.0827, 80.2707);

        DeliveryOrder order = createOrder(
                "O1", Priority.HIGH, "20",
                13.0900, 80.2800);

        List<VehicleRoute> routes =
                optimizer.optimize(
                        List.of(order),
                        List.of(vehicle2, vehicle1));

        VehicleRoute assignedRoute = routes.stream()
                .filter(route -> !route.getAssignedOrders().isEmpty())
                .findFirst()
                .orElseThrow();

        assertEquals("V1", assignedRoute.getVehicle().getVehicleId());
    }

    @Test
    void shouldNotModifyOriginalOrderList() {

        Vehicle vehicle = createVehicle(
                "V1", "100", 13.0827, 80.2707);

        DeliveryOrder low = createOrder(
                "O1", Priority.LOW, "20",
                13.0900, 80.2800);

        DeliveryOrder high = createOrder(
                "O2", Priority.HIGH, "30",
                13.1000, 80.2900);

        List<DeliveryOrder> originalOrders =
                new ArrayList<>(List.of(low, high));

        List<String> originalOrderIds = originalOrders.stream()
                .map(DeliveryOrder::getOrderId)
                .toList();

        optimizer.optimize(originalOrders, List.of(vehicle));

        List<String> updatedOrderIds = originalOrders.stream()
                .map(DeliveryOrder::getOrderId)
                .toList();

        assertEquals(originalOrderIds, updatedOrderIds);
    }

    @Test
    void shouldMaintainCapacityAndAvoidDuplicateOrderAssignments() {

        Vehicle vehicle1 = createVehicle(
                "V1", "40", 13.0827, 80.2707);

        Vehicle vehicle2 = createVehicle(
                "V2", "40", 13.0827, 80.2707);

        List<DeliveryOrder> orders = List.of(
                createOrder("O1", Priority.HIGH, "30",
                        13.0900, 80.2800),
                createOrder("O2", Priority.MEDIUM, "20",
                        13.1000, 80.2900),
                createOrder("O3", Priority.LOW, "20",
                        13.1100, 80.3000),
                createOrder("O4", Priority.LOW, "20",
                        13.1200, 80.3100)
        );

        List<VehicleRoute> routes =
                optimizer.optimize(orders, List.of(vehicle1, vehicle2));

        for (VehicleRoute route : routes) {
            assertTrue(
                    route.getTotalLoad().compareTo(
                            route.getVehicle().getCapacity()) <= 0,
                    "Vehicle capacity must never be exceeded");
        }

        List<String> assignedOrderIds = routes.stream()
                .flatMap(route -> route.getAssignedOrders().stream())
                .map(DeliveryOrder::getOrderId)
                .toList();

        long uniqueOrderCount = assignedOrderIds.stream()
                .distinct()
                .count();

        assertEquals(
                assignedOrderIds.size(),
                uniqueOrderCount,
                "An order must not be assigned to multiple vehicles");
    }
}


package com.logistics.dispatch.service;

import com.logistics.dispatch.dto.response.DispatchPlanResponse;
import com.logistics.dispatch.dto.response.OrderResponse;
import com.logistics.dispatch.dto.response.VehicleDispatchResponse;
import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Vehicle;
import com.logistics.dispatch.repository.OrderRepository;
import com.logistics.dispatch.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchOptimizer dispatchOptimizer;

    public DispatchService(
            OrderRepository orderRepository,
            VehicleRepository vehicleRepository,
            DispatchOptimizer dispatchOptimizer) {

        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.dispatchOptimizer = dispatchOptimizer;
    }

    @Transactional(readOnly = true)
    public DispatchPlanResponse generateDispatchPlan() {

        // Fetch all delivery orders and vehicles.
        List<DeliveryOrder> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Run the dispatch optimization algorithm.
        List<VehicleRoute> routes =
                dispatchOptimizer.optimize(orders, vehicles);

        // Convert optimized routes into response DTOs.
        List<VehicleDispatchResponse> dispatchPlan =
                routes.stream()
                        .map(this::mapToVehicleResponse)
                        .toList();

        // Collect IDs of all successfully assigned orders.
        Set<String> assignedOrderIds = routes.stream()
                .flatMap(route -> route.getAssignedOrders().stream())
                .map(DeliveryOrder::getOrderId)
                .collect(Collectors.toSet());

        // Identify orders that could not be assigned.
        List<OrderResponse> unassignedOrders = orders.stream()
                .filter(order ->
                        !assignedOrderIds.contains(order.getOrderId()))
                .map(this::mapToOrderResponse)
                .toList();

        // Return both assigned and unassigned orders.
        return new DispatchPlanResponse(
                dispatchPlan,
                unassignedOrders
        );
    }

    private VehicleDispatchResponse mapToVehicleResponse(
            VehicleRoute route) {

        List<OrderResponse> assignedOrders =
                route.getAssignedOrders()
                        .stream()
                        .map(this::mapToOrderResponse)
                        .toList();

        String formattedDistance = String.format(
                Locale.US,
                "%.2f km",
                route.getTotalDistanceKm()
        );

        return new VehicleDispatchResponse(
                route.getVehicle().getVehicleId(),
                route.getTotalLoad(),
                formattedDistance,
                assignedOrders
        );
    }

    private OrderResponse mapToOrderResponse(
            DeliveryOrder order) {

        return new OrderResponse(
                order.getOrderId(),
                order.getLatitude(),
                order.getLongitude(),
                order.getAddress(),
                order.getPackageWeight(),
                order.getPriority()
        );
    }
}

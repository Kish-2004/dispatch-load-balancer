
package com.logistics.dispatch.service;

import com.logistics.dispatch.dto.response.DispatchPlanResponse;
import com.logistics.dispatch.entity.DeliveryOrder;
import com.logistics.dispatch.entity.Priority;
import com.logistics.dispatch.entity.Vehicle;
import com.logistics.dispatch.repository.OrderRepository;
import com.logistics.dispatch.repository.VehicleRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DispatchOptimizer dispatchOptimizer;

    @InjectMocks
    private DispatchService dispatchService;

    private DeliveryOrder createOrder(
            String id, String weight, Priority priority) {

        DeliveryOrder order = new DeliveryOrder();
        order.setOrderId(id);
        order.setLatitude(28.6139);
        order.setLongitude(77.2090);
        order.setAddress("Delhi, India");
        order.setPackageWeight(new BigDecimal(weight));
        order.setPriority(priority);

        return order;
    }

    private Vehicle createVehicle() {

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId("VEH001");
        vehicle.setCapacity(new BigDecimal("100"));
        vehicle.setCurrentLatitude(28.7041);
        vehicle.setCurrentLongitude(77.1025);
        vehicle.setCurrentAddress("Karol Bagh, Delhi, India");

        return vehicle;
    }

    @Test
    void shouldReturnAssignedAndUnassignedOrders() {

        DeliveryOrder assigned =
                createOrder("ORD001", "20", Priority.HIGH);

        DeliveryOrder unassigned =
                createOrder("ORD002", "150", Priority.LOW);

        Vehicle vehicle = createVehicle();

        VehicleRoute route = new VehicleRoute(vehicle);

        route.addOrder(assigned, 0, 5.5);

        when(orderRepository.findAll())
                .thenReturn(List.of(assigned, unassigned));

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle));

        when(dispatchOptimizer.optimize(
                List.of(assigned, unassigned),
                List.of(vehicle)))
                .thenReturn(List.of(route));

        DispatchPlanResponse response =
                dispatchService.generateDispatchPlan();

        assertNotNull(response);

        assertEquals(1, response.dispatchPlan().size());

        assertEquals(
                "VEH001",
                response.dispatchPlan().get(0).vehicleId()
        );

        assertEquals(
                new BigDecimal("20"),
                response.dispatchPlan().get(0).totalLoad()
        );

        assertEquals(
                1,
                response.dispatchPlan().get(0)
                        .assignedOrders().size()
        );

        assertEquals(
                1,
                response.unassignedOrders().size()
        );

        assertEquals(
                "ORD002",
                response.unassignedOrders().get(0).orderId()
        );
    }

    @Test
    void shouldReturnEmptyPlanWhenNoOrdersOrVehiclesExist() {

        when(orderRepository.findAll())
                .thenReturn(List.of());

        when(vehicleRepository.findAll())
                .thenReturn(List.of());

        when(dispatchOptimizer.optimize(
                List.of(), List.of()))
                .thenReturn(List.of());

        DispatchPlanResponse response =
                dispatchService.generateDispatchPlan();

        assertNotNull(response);
        assertTrue(response.dispatchPlan().isEmpty());
        assertTrue(response.unassignedOrders().isEmpty());
    }

    @Test
    void shouldIncludeAllOrdersAsUnassignedWhenNoVehiclesExist() {

        DeliveryOrder order =
                createOrder("ORD001", "25", Priority.HIGH);

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        when(vehicleRepository.findAll())
                .thenReturn(List.of());

        when(dispatchOptimizer.optimize(
                List.of(order), List.of()))
                .thenReturn(List.of());

        DispatchPlanResponse response =
                dispatchService.generateDispatchPlan();

        assertTrue(response.dispatchPlan().isEmpty());

        assertEquals(1, response.unassignedOrders().size());

        assertEquals(
                "ORD001",
                response.unassignedOrders().get(0).orderId()
        );
    }
}
